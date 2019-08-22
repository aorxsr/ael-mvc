package org.ael.mvc.template;

import lombok.Data;
import org.ael.mvc.http.WebContent;

import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 19:50
 */
@Data
public class ModelAndView {

	String view;

	Map<String, Object> model;

	WebContent webContent;

}
