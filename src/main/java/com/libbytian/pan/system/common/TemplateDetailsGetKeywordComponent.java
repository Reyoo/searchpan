package com.libbytian.pan.system.common;

import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SunQi
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TemplateDetailsGetKeywordComponent {

    private final ISystemTemDetailsService systemTemDetailsService;

    public SystemTemDetailsModel getUserKeywordDetail(SystemUserModel systemUserModel, String keyword) throws Exception {
        List<SystemTemDetailsModel> temDetailsModelList = systemTemDetailsService.getTemDetailsWithUser(systemUserModel);
        SystemTemDetailsModel systemTemDetailsModel =  temDetailsModelList.parallelStream().filter(t -> t.keyword().equals(keyword)).collect(Collectors.toList()).get(0);
        return systemTemDetailsModel;
    }

}
