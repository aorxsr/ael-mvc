package org.ael.template;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ael.http.WebContent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 19:50
 */
@Data
@NoArgsConstructor
public class ModelAndView {

	String view;

	Map<String, Object> model = new HashMap<>();

	WebContent webContent;

	public ModelAndView(String view) {
		this.view = view;
	}

	public void addModelData(String name, Object value) {
		this.model.put(name, value);
	}

}
