package org.ael.mvc.container;

import cn.hutool.core.util.ClassUtil;
import org.ael.mvc.Ael;
import org.ael.mvc.commons.StringUtils;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.container.annotation.Subassembly;
import org.ael.mvc.container.bean.Container;
import org.ael.mvc.container.exception.SubassemblyExistException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleContainer {

    private Ael ael;

    private Map<String, Container> containers = new ConcurrentHashMap<>(32);

    private Map<String, Class> subassemblys = new ConcurrentHashMap<>(32);

    private Class<Subassembly> subassemblyClass = Subassembly.class;

    public SimpleContainer(Ael ael) {
        this.ael = ael;
    }

    public void initContainer() {
        String scanPackage = ael.getEnvironment().getString(EnvironmentConstant.SCAN_PACKAGE);
        if (StringUtils.isNotEmpty(scanPackage)) {
            ClassUtil.scanPackage(scanPackage).forEach(this::existenceSubassembly);
        }
    }

    private void existenceSubassembly(Class<?> clazz) throws SubassemblyExistException {
        Subassembly subassembly = clazz.getDeclaredAnnotation(subassemblyClass);
        if (null == subassembly) {
            return;
        } else {
            String name = subassembly.name();
            if (StringUtils.isEmpty(name)) {
                // 获取类名,转小写
                String subassemblyName = getClassNameToLower(clazz);
                if (subassemblys.containsKey(subassemblyName)) {
                    throw new SubassemblyExistException("Subassembly '" + subassemblyName + "' exist. ");
                } else {
                    subassemblys.put(subassemblyName, clazz);
                }
            } else {
                // 使用name判断
                if (subassemblys.containsKey(name)) {
                    throw new SubassemblyExistException("Subassembly name : '" + name + "' exist. ");
                } else {
                    subassemblys.put(name, clazz);
                }
            }
        }
    }

    private String getClassNameToLower(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        String one = simpleName.substring(0, 1);
        return (one.toLowerCase()) + (simpleName.substring(1));
    }


}
