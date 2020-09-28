package com.libbytian.pan.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetails;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemTmplToTmplDetailsService;
import com.libbytian.pan.system.util.ReadOrWriteExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemTemDetailsServiceImpl extends ServiceImpl<SystemTemDetailsMapper, SystemTemDetailsModel> implements ISystemTemDetailsService {


    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;


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
            SystemTemToTemdetails temToTemdetails= SystemTemToTemdetails.builder().templateid(templateId).templatedetailsid(uuid).build();
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
            SystemTemToTemdetails temToDetails = SystemTemToTemdetails.builder().templateid(templateId).templatedetailsid(systemTemDetailsModel.getTemdetailsId()).build();
            iSystemTmplToTmplDetailsService.save(temToDetails);

        }
        return result;
    }

    @Override
    public int updateTemDetails(SystemTemDetailsModel systemTemDetailsModel) throws Exception {

        if(systemTemDetailsModel.getTemdetailsId() == null){
            throw new Exception("未绑定id，请重新输入");
        }
        if(systemTemDetailsModel.getKeyword() == null || systemTemDetailsModel.getKeyword().isEmpty()){
            throw new Exception("请填写关键词");
        }
        if(systemTemDetailsModel.getKeywordToValue() == null || systemTemDetailsModel.getKeywordToValue().isEmpty()){
            throw new Exception("请填写回复内容");
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        systemTemDetailsModel.setCreatetime(localDateTime);

        int result = systemTemDetailsMapper.updateById(systemTemDetailsModel);

        return result;
    }


}
