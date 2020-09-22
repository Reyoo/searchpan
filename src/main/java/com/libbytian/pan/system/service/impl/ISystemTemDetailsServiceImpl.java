package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemTemDetailsMapper;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.TemToTemDetailsModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ITemToTemDetailsService;
import com.libbytian.pan.system.util.ReadOrWriteExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ISystemTemDetailsServiceImpl extends ServiceImpl<SystemTemDetailsMapper, SystemTemDetailsModel> implements ISystemTemDetailsService {


    private final SystemTemDetailsMapper systemTemDetailsMapper;
    private final ITemToTemDetailsService iTemToTemDetailsService;


    /**
     *
     * @param filename
     * @param inputStream
     * @return
     */
    @Override
    public int exportExceltoDb(String filename, InputStream inputStream)  throws Exception{

        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String columns[] = {"id","question","answer","date_time","isTop"};
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
        //遍历解析出来的list
        for (Map<String,String> map : list) {
            for (Map.Entry<String,String> entry : map.entrySet()) {
                System.out.print(entry.getKey()+":"+entry.getValue()+",");
            }
            System.out.println("==============");
        }


        return 0;
    }


    /**
     *
     * @param keyword
     * @param keywordToValue
     * @return
     */
    @Override
    public int addTemDetails(String keyword, String keywordToValue) throws Exception  {

        SystemTemDetailsModel user = new SystemTemDetailsModel();
        LocalDateTime localDateTime = LocalDateTime.now();
        user.setKeywordToValue(keywordToValue);
        user.setKeyword(keyword);
        user.setCreatetime(localDateTime);
        int result = systemTemDetailsMapper.addTemDetails(keyword, keywordToValue, localDateTime, user);
        int id = user.getTemdetailsId();
        if (result == 1) {
            TemToTemDetailsModel temToDetails = TemToTemDetailsModel.builder().templateid(1).templatedetailsid(id).build();
            iTemToTemDetailsService.save(temToDetails);
        }
        return result;
    }


    @Override
    public IPage<SystemTemDetailsModel> findTemDetails(Page page)  throws Exception {

        return systemTemDetailsMapper.selectTemDetails(page);
    }
}
