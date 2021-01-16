package com.libbytian.pan.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Override
    public List<SystemTemDetailsModel> getTemDetails(SystemTemplateModel systemTemplateModel) throws Exception {
        return systemTemDetailsMapper.getTemDetails(systemTemplateModel);
    }


    /**
     * @param filename
     * @param inputStream
     * @return
     */
    @Override
    public int exportExceltoDb(String filename, InputStream inputStream, String templateId) throws Exception {
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
            uuidList.add(uuid);
            systemTemDetailsModelList.add(systemTemDetailsModel);
        }


        this.saveBatch(systemTemDetailsModelList);

        for (String uuid : uuidList) {
            SystemTemToTemdetail temToTemdetails = SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(uuid).build();
            iSystemTmplToTmplDetailsService.save(temToTemdetails);
        }

        return 0;
    }


    @Override
    public int addTemDetails(SystemTemDetailsModel systemTemDetailsModel, String templateId) throws Exception {

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
        return result;
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
        String temdetailsId =temdetailsIds.get(0);
        SystemTemplateModel systemTemplateModel = iSystemTemplateService.getTemplateById(temdetailsId);

        //sheet名
        String sheetName = systemTemplateModel.getTemplatename();
        String fileName = sheetName + ".xls";
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String[][] content = new String[systemTemDetailsModelList.size()][title.length];
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
            this.setResponseHeader(httpServletResponse, fileName);
            OutputStream os = httpServletResponse.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public List<SystemTemDetailsModel> listTemDetailsObjectsByWord(SystemTemDetailsModel systemTemDetailsModel) {
        return systemTemDetailsMapper.listTemDetailsObjectsByWord(systemTemDetailsModel);
    }

    @Override

    public void defaultSave(String templateId) {

        List<SystemTemDetailsModel> defultModel = systemTemDetailsMapper.defultTemDetails();
        //待优化
//        //新增模板设置默认关键字
//        List<SystemTemDetailsModel> detailist = new ArrayList<>();
//        SystemTemDetailsModel secretKey = new SystemTemDetailsModel();
//        SystemTemDetailsModel sleepDetails = new SystemTemDetailsModel();
//        SystemTemDetailsModel headAdvert = new SystemTemDetailsModel();
//        SystemTemDetailsModel endAdvert = new SystemTemDetailsModel();
//        SystemTemDetailsModel hideResource = new SystemTemDetailsModel();
//        SystemTemDetailsModel hideReply = new SystemTemDetailsModel();
//        SystemTemDetailsModel headWeb = new SystemTemDetailsModel();
//        SystemTemDetailsModel endWeb = new SystemTemDetailsModel();
//
//        sleepDetails.setKeyword("维护内容");
//        sleepDetails.setKeywordToValue("维护时间内回复内容");
//        headAdvert.setKeyword("头部广告");
//        headAdvert.setKeywordToValue("微信回复头部广告（删除可去掉）");
//        endAdvert.setKeyword("底部广告");
//        endAdvert.setKeywordToValue("微信回复底部广告（删除可去掉）");
//        hideResource.setKeyword("隐藏资源");
//        hideResource.setKeywordToValue("隐藏资源名称");
//        hideReply.setKeyword("隐藏回复");
//        hideReply.setKeywordToValue("隐藏资源后返回给粉丝内容");
//        headWeb.setKeyword("头部提示web");
//        headWeb.setKeywordToValue("<style type=\"text/css\">\n"
//        endWeb.setKeyword("底部提示web");
//        endWeb.setKeywordToValue("web页面底部提示内容");
//        secretKey.setKeyword("秘钥回复");
//        secretKey.setKeywordToValue("粉丝口令回复内容");
//
//        detailist.add(sleepDetails);
//        detailist.add(headAdvert);
//        detailist.add(endAdvert);
//        detailist.add(hideResource);
//        detailist.add(hideReply);
//        detailist.add(headWeb);
//        detailist.add(endWeb);
//        detailist.add(secretKey);

        /**
 * 需要改成批量插入
 */
        for (int i = 0; i < defultModel.size(); i++) {
            SystemTemDetailsModel details = defultModel.get(i);
            details.setTemdetailsId(UUID.randomUUID().toString());
            details.setCreatetime(LocalDateTime.now());
            systemTemDetailsMapper.insertSystemTemDetails(details);
            //用户模板绑定模板详情
            SystemTemToTemdetail temToTemdetail = SystemTemToTemdetail.builder().templateid(templateId).templatedetailsid(details.getTemdetailsId()).build();
            iSystemTmplToTmplDetailsService.save(temToTemdetail);
        }

    }

    /**
     * 根据用户 及关键词返回模板详细
     *
     * @param username
     * @param keyword
     * @return
     */
    @Override
    public SystemTemDetailsModel getUserKeywordDetail(String username, String keyword) {
        return systemTemDetailsMapper.selectUserKeywordDetail(username, keyword);

    }

    @Override
    public int dropTemplateDetailsByUser(SystemUserModel systemUserModel) throws Exception {

        if (getTemDetailsWithUser(systemUserModel).size() < 0) {
            return 0;
        }else {
            log.info("开始删除");
            systemTemDetailsMapper.deleteTemplateDetailsByUser(systemUserModel);
        }


        return 0;
    }


    /**
     * 删除用户模板下详情
     * @param temdetailsId
     * @return
     */
    @Override
    public int deleteTemplateDetails(List<String> temdetailsId) {
       return systemTemDetailsMapper.deleteTemplateDetails(temdetailsId);
    }

}
