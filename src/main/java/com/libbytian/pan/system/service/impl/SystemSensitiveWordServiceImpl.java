package com.libbytian.pan.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.enums.SensitiveWordsType;
import com.libbytian.pan.system.mapper.SensitiveWordMapper;
import com.libbytian.pan.system.model.SystemRecordSensitiveModel;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import com.libbytian.pan.system.service.ISystemRecordSensitiveService;
import com.libbytian.pan.system.service.ISystemSensitiveWordService;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.system.util.ReadOrWriteExcelUtil;
import com.libbytian.pan.system.util.sensitive.SensitiveWordEngine;
import com.libbytian.pan.system.config.SensitiveWordInit;
import com.libbytian.pan.system.model.SensitiveWordModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.util.sensitive
 * @ClassName: SensitiveWordService
 * @Author: sun71
 * @Description:
 * @Date: 2020/10/20 10:20
 * @Version: 1.0
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SystemSensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWordModel> implements ISystemSensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;

    private final ISystemRecordSensitiveService iSystemRecordSensitiveService;

    private final ISystemUserService iSystemUserService;



    /**
     * @Description: 是否包含敏感词
     * @author lc
     */
    @Override
    public Boolean isContaintSensitiveWord(SystemTemDetailsModel systemTemDetailsModel){
        log.info("[通用短信请求]是否包含敏感词验证, 短信内容为: {}", systemTemDetailsModel.getKeyword());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Boolean boo = false;
        try {

            // 构建敏感词库

            // 传入SensitivewordEngine类中的敏感词库
            SensitiveWordEngine.sensitiveWordMap =  SensitiveWordInit.sensitiveWordMap;
            boo = SensitiveWordEngine.isContaintSensitiveWord(systemTemDetailsModel.getKeyword(), 2);

            //敏感词存入记录库
            SystemRecordSensitiveModel record = new SystemRecordSensitiveModel();
            record.setRecordSaveTime(LocalDateTime.now());
            record.setRecordWord(systemTemDetailsModel.getKeyword());

            SensitiveWordModel sensitiveWordModel =  new SensitiveWordModel();
            sensitiveWordModel.setWord(systemTemDetailsModel.getKeyword());

            //查询到type存入记录库
            SensitiveWordsType type = sensitiveWordMapper.listSensitiveWordObjects(sensitiveWordModel).get(0).getType();
            record.setRecordType(type);

            //通过templateId查询到username
            record.setRecordUsername(iSystemUserService.getUserByUerToTemplate(systemTemDetailsModel.getTemplateId()).getUsername());

            iSystemRecordSensitiveService.save(record);

        } catch (Exception e) {
            log.error("[通用短信请求]是否包含敏感词验证异常！{}", e.getMessage());
        }
        return boo;
    }

    /**
     * @Description: 获取敏感词内容
     * @author lc
     */
    @Override
    public Set<String> getSensitiveWord(SystemTemDetailsModel systemTemDetailsModel){
        log.info("[通用短信请求]获取敏感词内容, 短信内容为: {}", systemTemDetailsModel.getKeyword());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Set<String> sensitiveWordList = null;
        try {
//            paramMap.put("valid", PublicEnum.YES.getCode());
//            paramMap.put("type", PublicEnum.N.getCode())
            // 从数据库中获取敏感词对象集合
            // 初始化敏感词库对象
            // 构建敏感词库
            // 传入SensitivewordEngine类中的敏感词库
            SensitiveWordEngine.sensitiveWordMap =  SensitiveWordInit.sensitiveWordMap;

            sensitiveWordList = SensitiveWordEngine.getSensitiveWord(systemTemDetailsModel.getKeyword(), 2);

            log.info("[通用短信请求]获取敏感词内容, 敏感词为: {}", sensitiveWordList);

            // 留存拦截记录
//            SmsBlackInterceptRecord record = new SmsBlackInterceptRecord(baseMessage, sensitiveWordList.toString());
//            interceptRecordService.insert(record);
        } catch (Exception e) {
            log.error("[通用短信请求]获取敏感词内容异常！{}", e.getMessage());
        }
        return sensitiveWordList;
    }

    /**
     * 查询敏感词
     * @param sensitiveWordModel
     * @return
     */
    @Override
    public List<SensitiveWordModel> listSensitiveWordObjects(SensitiveWordModel sensitiveWordModel) {
        return sensitiveWordMapper.listSensitiveWordObjects(sensitiveWordModel);
    }



    @Override
    public int exportExceltoDb(String filename, InputStream inputStream) throws Exception {

        List<SensitiveWordModel> systemTemDetailsModelList = new ArrayList<>();

        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String columns[] = {"T_BASE_SENSITIVEWORDS_ID","SENSITIVETYPE","SENSITIVETOPIC","SENSITIVEWORDS"};
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

            SensitiveWordModel sensitiveWordModel = new SensitiveWordModel();

            boolean boo = false;

            // 传入SensitivewordEngine类中的敏感词库
            SensitiveWordEngine.sensitiveWordMap =  SensitiveWordInit.sensitiveWordMap;


                for (Map.Entry<String,String> entry : map.entrySet()) {


                    if(entry.getKey().equals("SENSITIVEWORDS")){

                        sensitiveWordModel.setWord(entry.getValue());

                    }
                    if(entry.getKey().equals("SENSITIVETYPE")){

                        switch (entry.getValue()){
                            case "色情":
                                sensitiveWordModel.setType(SensitiveWordsType.PORNO);
                                break;
                            case "政治":
                                sensitiveWordModel.setType(SensitiveWordsType.POLITICS);
                                break;
                            case "暴恐":
                                sensitiveWordModel.setType(SensitiveWordsType.TERROR);
                                break;
                            case "民生":
                                sensitiveWordModel.setType(SensitiveWordsType.LIVELIHOOD);
                                break;
                            case "反动":
                                sensitiveWordModel.setType(SensitiveWordsType.REACTION);
                                break;
                            case "贪腐":
                                sensitiveWordModel.setType(SensitiveWordsType.CORRUPTION);
                                break;
                            case "其他":
                                sensitiveWordModel.setType(SensitiveWordsType.OTHERS);
                                break;

                        }

                    }

            }


            if(sensitiveWordModel.getWord() != null){
                boo = SensitiveWordEngine.isContaintSensitiveWord(sensitiveWordModel.getWord(), 2);
                if (boo){
                    continue;
                }

            }

            sensitiveWordModel.setCreateTime(LocalDateTime.now());
            sensitiveWordModel.setCreator("admin");


            systemTemDetailsModelList.add(sensitiveWordModel);
        }

        this.saveBatch(systemTemDetailsModelList);

        return 0;
    }

    @Override
    public int removeRepeat() {
      return  sensitiveWordMapper.removeRepeat();
    }

}
