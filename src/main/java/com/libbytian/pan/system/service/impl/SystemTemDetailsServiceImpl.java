package com.libbytian.pan.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemTemplateService;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import com.libbytian.pan.system.util.ReadOrWriteExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板详细业务实现类
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemTemDetailsServiceImpl extends ServiceImpl<SystemTemDetailsMapper, SystemTemDetailsModel> implements ISystemTemDetailsService {


    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;
    private final ISystemTemplateService iSystemTemplateService;


    /**
     *
     * @param filename
     * @param inputStream
     * @return
     */
    @Override
    public int exportExceltoDb(String filename, InputStream inputStream ,String templateId)  throws Exception{
        List<SystemTemDetailsModel> systemTemDetailsModelList = new ArrayList<>();
        List<String> uuidList = new ArrayList<>();

        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String columns[] = {"id","question","answer","userid","date_time","isTop"};
        wb = ReadOrWriteExcelUtil.readExcel(filename, inputStream);
        if(wb != null){
            //用来存放表中数据
            list = new ArrayList<Map<String,String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 1; i<rownum; i++) {
                Map<String,String> map = new LinkedHashMap<String,String>();
                row = sheet.getRow(i);
                if(row !=null){
                    for (int j=0;j<colnum;j++){
                        cellData = (String) ReadOrWriteExcelUtil.getCellFormatValue(row.getCell(j));
                        map.put(columns[j], cellData);
                    }
                }else{
                    break;
                }
                list.add(map);
            }
        }

        //String转LocalDateTime格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        //遍历解析出来的list
        for (Map<String,String> map : list) {
            SystemTemDetailsModel systemTemDetailsModel = new SystemTemDetailsModel();

            for (Map.Entry<String,String> entry : map.entrySet()) {
                if(entry.getKey().equals("question")){
                    systemTemDetailsModel.setKeyword(entry.getValue());
                }
                if(entry.getKey().equals("answer")){
                    systemTemDetailsModel.setKeywordToValue(entry.getValue());
                }
                if(entry.getKey().equals("date_time")){

                    LocalDateTime time = LocalDateTime.parse(entry.getValue(),dateTimeFormatter);
                    systemTemDetailsModel.setCreatetime(time);
                }
                if(entry.getKey().equals("isTop")){
                    systemTemDetailsModel.setTemdetailsstatus(Integer.valueOf(entry.getValue()));
                }
            }

            String uuid = UUID.randomUUID().toString();
            systemTemDetailsModel.setTemdetailsId(uuid);
            uuidList.add(uuid);
            systemTemDetailsModelList.add(systemTemDetailsModel);
        }


        this.saveBatch(systemTemDetailsModelList);

        for (String uuid : uuidList) {
            SystemTemToTemdetail temToTemdetails= SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(uuid).build();
            iSystemTmplToTmplDetailsService.save(temToTemdetails);
        }

        return 0;
    }



    @Override
    public int addTemDetails(SystemTemDetailsModel systemTemDetailsModel, String templateId) throws Exception {

        systemTemDetailsModel.setCreatetime(LocalDateTime.now());
        systemTemDetailsModel.setTemdetailsId(UUID.randomUUID().toString());
        systemTemDetailsModel.setTemdetailsstatus(0);

        //插入模板详情表
        int result = systemTemDetailsMapper.insert(systemTemDetailsModel);

        if (result == 1) {
            //插入模板_模板详情表
            SystemTemToTemdetail temToDetails = SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(systemTemDetailsModel.getTemdetailsId()).build();
            iSystemTmplToTmplDetailsService.save(temToDetails);

        }
        return result;
    }


    /**
     * 导出excel
     * @param httpServletRequest
     * @param templateId
     * @throws Exception
     */
    @Override
    public void exportTemDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,String templateId) throws Exception {

        //1 拿着模板号 先去关联表中取出所有模板详细ID
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("template_id", templateId);
        List<SystemTemToTemdetail> systemTemToTemdetails = new ArrayList<>();
        systemTemToTemdetails = iSystemTmplToTmplDetailsService.list(queryWrapper);
        //2. 获取所有详细ID list
        List<String> detailsIdList = systemTemToTemdetails.stream().map(SystemTemToTemdetail::getTemplatedetailsid).collect(Collectors.toList());
        //得到了详细数据
        List<SystemTemDetailsModel> systemTemDetailsModelList = this.listByIds(detailsIdList);
        //excel标题
        String title[] = {"id","question","answer","userid","date_time","isTop"};
        //excel文件名
        String fileName = "学生信息表"+System.currentTimeMillis()+".xls";
        SystemTemplateModel systemTemplateModel = iSystemTemplateService.getById(templateId);
         //sheet名
        String sheetName = systemTemplateModel.getTemplatename();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String[][] content=new String[systemTemDetailsModelList.size()][title.length];
        for (int i = 0; i < systemTemDetailsModelList.size(); i++) {
            SystemTemDetailsModel obj = systemTemDetailsModelList.get(i);
            content[i][0] = obj.getTemdetailsId();
            content[i][1] = obj.getKeyword();
            content[i][2] = obj.getKeywordToValue();
            content[i][3] = httpServletRequest.getRemoteUser();
            content[i][4] = obj.getCreatetime().format(pattern);
            content[i][5] = obj.getTemdetailsstatus().toString();
        }

        //创建HSSFWorkbook
        HSSFWorkbook wb = ReadOrWriteExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        //响应到客户端
        try {
            this.setResponseHeader(httpServletResponse,fileName);
            OutputStream os = httpServletResponse.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
            } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     *@Author zxl
     *@Description 发送响应流方法
     *@Date 13:40 2019/2/11
     *@Param [response, fileName]
     *@Return void
     */
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 根据用户信息获取 启用状态的模板详细List
     *
     * @param systemUserModel
     */
    @Override
    @Cacheable(value = "userTemplateDetail",key = "#systemUserModel.username")
    public List<SystemTemDetailsModel> getTemDetailsWithUser(SystemUserModel systemUserModel) throws Exception {
        List<SystemTemDetailsModel> systemTemDetailsModels = new ArrayList<>();
        try {
            systemTemDetailsModels = systemTemDetailsMapper.findTemDetailsByUser(systemUserModel);
        }catch (Exception e){
            e.printStackTrace();
        }
        return systemTemDetailsModels;
    }

}
