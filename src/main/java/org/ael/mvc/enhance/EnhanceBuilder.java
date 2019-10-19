package org.ael.mvc.enhance;

import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.Ael;
import org.ael.mvc.annotation.Configuration;
import org.ael.mvc.commons.StringUtils;
import org.ael.mvc.enhance.annotation.Enhance;
import org.ael.mvc.enhance.annotation.EnhanceEnable;
import org.ael.mvc.handler.init.AbstractInitHandler;
import org.ael.mvc.http.WebContent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author aorxsr
 * @date 2019/10/19
 */
@Slf4j
@Configuration(order = 3)
public class EnhanceBuilder extends AbstractInitHandler {

    Class<Enhance> enhanceClass = Enhance.class;
    Class<EnhanceEnable> enhanceEnableClass = EnhanceEnable.class;
    Class<AbstractEnhance> abstractEnhanceClass = AbstractEnhance.class;

    /**
     * url enhance, enhanceInfo {@link EnhanceInfo}
     */
    List<EnhanceInfo> enhanceInfos = new LinkedList<>();

    @Override
    public void init(Ael ael) {
        log.info("enhance init start ...");
        ael.getScanClass()
                .stream()
                .filter(this::enhanceFilter)
                .forEach(this::enhanceExecute);
        ael.setEnhanceHandler(new EnhanceHandler(this));
        log.info("enhance init end ...");
    }

    private boolean enhanceFilter(Class<?> clazz) {
        if (clazz.isAnnotationPresent(enhanceClass)) {
            try {
                clazz.asSubclass(abstractEnhanceClass);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private void enhanceExecute(Class<?> clazz) {
        Enhance enhance = clazz.getDeclaredAnnotation(enhanceClass);
        String url = enhance.url();
        if (StringUtils.isEmpty(url)) {
            log.error("enhance class:" + clazz.getSimpleName() + " not url.");
        }
        // url
        EnhanceInfo enhanceInfo = new EnhanceInfo();
        try {
            // http enhance type
            enhanceInfo.setHttpMethod(enhance.httpMethod());
            // instance
            Object instance = clazz.newInstance();
            enhanceInfo.setTarget(instance);
            boolean hasAdd = false;
            // before
            Method beforeMethod = clazz.getMethod("before", WebContent.class);
            EnhanceEnable beforeEnhance = beforeMethod.getDeclaredAnnotation(enhanceEnableClass);
            if (null != beforeEnhance) {
                hasAdd = true;
                enhanceInfo.setBeforeMethod(beforeMethod);
            }
            // after
            Method afterMethod = clazz.getMethod("after", WebContent.class);
            EnhanceEnable afterEnhance = afterMethod.getDeclaredAnnotation(enhanceEnableClass);
            if (null != afterEnhance) {
                hasAdd = true;
                enhanceInfo.setAfterMethod(afterMethod);
            }
            if (hasAdd) {
                enhanceInfos.add(enhanceInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
