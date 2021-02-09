package com.libbytian.pan.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.libbytian.pan.wechat.constant.TemplateKeywordConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 模板详细业务实现类
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemTemDetailsServiceImpl extends ServiceImpl<SystemTemDetailsMapper, SystemTemDetailsModel> implements ISystemTemDetailsService {


    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;
    private final ISystemTemplateService iSystemTemplateService;


    @Override
    public IPage<SystemTemDetailsModel> findTemDetailsPage(Page page, String templateId) throws Exception {
        return systemTemDetailsMapper.selectTemDetailsPage(page, templateId);
    }

    /**
     * 不分页查询
     *
     * @param
     * @return
     * @throws Exception
     */

    public List<SystemTemDetailsModel> getTemDetails(SystemTemplateModel systemTemplateModel) throws Exception {
        return systemTemDetailsMapper.getTemDetails(systemTemplateModel);
    }


    /**
     * @param filename
     * @param inputStream
     * @return
     */
    @Override
    public List<SystemTemDetailsModel> exportExceltoDb(String filename, InputStream inputStream, String templateId,String username) throws Exception {
        List<SystemTemDetailsModel> systemTemDetailsModelList = new ArrayList<>();
        List<String> uuidList = new ArrayList<>();

        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        String columns[] = {"id", "question", "answer", "userid", "date_time", "isTop"};
        wb = ReadOrWriteExcelUtil.readExcel(filename, inputStream);
        if (wb != null) {
            //用来存放表中数据
            list = new ArrayList<Map<String, String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 1; i < rownum; i++) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < colnum; j++) {
                        cellData = (String) ReadOrWriteExcelUtil.getCellFormatValue(row.getCell(j));
                        map.put(columns[j], cellData);
                    }
                } else {
                    break;
                }
                list.add(map);
            }
        }

        //String转LocalDateTime格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        //遍历解析出来的list
        for (Map<String, String> map : list) {
            SystemTemDetailsModel systemTemDetailsModel = new SystemTemDetailsModel();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equals("question")) {
                    systemTemDetailsModel.setKeyword(entry.getValue());
                }
                if (entry.getKey().equals("answer")) {
                    systemTemDetailsModel.setKeywordToValue(entry.getValue());
                }
                if (entry.getKey().equals("date_time")) {

                    LocalDateTime time = LocalDateTime.parse(entry.getValue(), dateTimeFormatter);
                    systemTemDetailsModel.setCreatetime(time);
                }
                if (entry.getKey().equals("isTop")) {
                    systemTemDetailsModel.setTemdetailsstatus(Boolean.valueOf(entry.getValue()));
                }
            }

            String uuid = UUID.randomUUID(true).toString();
            systemTemDetailsModel.setTemdetailsId(uuid);
            systemTemDetailsModel.setEnableFlag(true);
            uuidList.add(uuid);
            systemTemDetailsModelList.add(systemTemDetailsModel);
        }

        //插入模板详细表   这个地方 不应该用mybatisPlus封装的
        this.saveBatch(systemTemDetailsModelList);

        //插入模板详细与模板关联表
        for (String uuid : uuidList) {
            SystemTemToTemdetail temToTemdetails = SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(uuid).build();
            iSystemTmplToTmplDetailsService.save(temToTemdetails);
        }

        return systemTemDetailsMapper.findTemDetailsByUser(new SystemUserModel(username));
    }


    @Override
    public  List<SystemTemDetailsModel> addTemDetails(SystemTemDetailsModel systemTemDetailsModel, String templateId,String username) throws Exception {

        systemTemDetailsModel.setCreatetime(LocalDateTime.now());
        systemTemDetailsModel.setTemdetailsId(UUID.randomUUID().toString());
        systemTemDetailsModel.setTemdetailsstatus(false);
        systemTemDetailsModel.setEnableFlag(true);

        //插入模板详情表
        int result = systemTemDetailsMapper.insert(systemTemDetailsModel);

        if (result == 1) {
            //插入模板_模板详情表
            SystemTemToTemdetail temToDetails = SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(systemTemDetailsModel.getTemdetailsId()).build();
            iSystemTmplToTmplDetailsService.save(temToDetails);
        }
        return systemTemDetailsMapper.findTemDetailsByUser(new SystemUserModel(username));
    }


    /**
     * 导出excel
     *
     * @param httpServletRequest
     * @param temdetailsIds
     * @throws Exception
     */
    @Override
    public void exportTemDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, List<String> temdetailsIds) throws Exception {

        //得到了详细数据
        List<SystemTemDetailsModel> systemTemDetailsModelList = this.listByIds(temdetailsIds);

        //excel标题
        String title[] = {"id", "question", "answer", "userid", "date_time", "isTop"};
        //excel文件名
        String temdetailsId = temdetailsIds.get(0);
        SystemTemplateModel systemTemplateModel = iSystemTemplateService.getTemplateById(temdetailsId);

        //sheet名
        String sheetName = systemTemplateModel.getTemplatename();
        String fileName = sheetName + ".xls";
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String[][] content = new String[systemTemDetailsModelList.size()][title.length];
        for (int i = 0; i < systemTemDetailsModelList.size(); i++) {

            SystemTemDetailsModel obj = systemTemDetailsModelList.get(i);
            if(hasTemplateKeyWord(obj.getKeyword())){
                continue;
            }
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
            this.setResponseHeader(httpServletResponse, fileName);
            OutputStream os = httpServletResponse.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    boolean hasTemplateKeyWord(String keyword){

        if(keyword.equals(TemplateKeywordConstant.KEY_CONTENT)
                || keyword.equals(TemplateKeywordConstant.SECRET_CONTENT)
                || keyword.equals(TemplateKeywordConstant.PRESERVE_CONTENT)
                || keyword.equals(TemplateKeywordConstant.END_WEB)
                || keyword.equals(TemplateKeywordConstant.HEAD_WEB)
                || keyword.equals(TemplateKeywordConstant.TAIL_ADVS)
                || keyword.equals(TemplateKeywordConstant.TOP_ADVS)
                || keyword.equals(TemplateKeywordConstant.First_Like)
                || keyword.equals(TemplateKeywordConstant.SECRET_REPLY)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }








    /**
     * @Author zxl
     * @Description 发送响应流方法
     * @Date 13:40 2019/2/11
     * @Param [response, fileName]
     * @Return void
     */
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
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
    public List<SystemTemDetailsModel> getTemDetailsWithUser(SystemUserModel systemUserModel) throws Exception {
        List<SystemTemDetailsModel> systemTemDetailsModels = new ArrayList<>();
        try {
            systemTemDetailsModels = systemTemDetailsMapper.findTemDetailsByUser(systemUserModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return systemTemDetailsModels;
    }

    @Override
    public List<SystemTemDetailsModel> updateTempDetailsWithModel(SystemTemDetailsModel systemTemDetailsModel, String username) throws Exception {
        systemTemDetailsMapper.updateTempDetailsWithModel(systemTemDetailsModel);
        return systemTemDetailsMapper.findTemDetailsByUser(new SystemUserModel(username));
    }

    @Override
    public List<SystemTemDetailsModel> listTemDetailsObjectsByWord(SystemTemDetailsModel systemTemDetailsModel) {
        return systemTemDetailsMapper.listTemDetailsObjectsByWord(systemTemDetailsModel);
    }

    @Override

    public void defaultSave(String templateId) {

        List<SystemTemDetailsModel> detailist = systemTemDetailsMapper.defultTemDetails();

/**
 * 需要改成批量插入
 */
        for (int i = 0; i < detailist.size(); i++) {
            SystemTemDetailsModel details = detailist.get(i);
            details.setTemdetailsId(UUID.randomUUID().toString());
            details.setCreatetime(LocalDateTime.now());
            details.setTemdetailsstatus(false);
            details.setEnableFlag(false);
            details.setShowOrder(1);
            systemTemDetailsMapper.insertSystemTemDetails(details);
            //用户模板绑定模板详情
            SystemTemToTemdetail temToTemdetail = SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(details.getTemdetailsId()).build();
            iSystemTmplToTmplDetailsService.save(temToTemdetail);
        }

    }


    /**
     * 根据用户 及关键词返回模板详细
     * @param systemUserModel
     * @param keyword
     * @return
     */


    /**
     * 删除用户模板下详情
     *
     * @param temdetailsId
     * @return
     */
    @Override
    public int deleteTemplateDetails(List<String> temdetailsId,String username) {
        return systemTemDetailsMapper.deleteTemplateDetails(temdetailsId);
    }

}
