package org.ael.mvc.route;

import org.ael.mvc.annotation.RequestParam;
import org.ael.mvc.annotation.ResponseJson;

public interface RouteConstant {

    Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;

    Class<ResponseJson> RESPONSE_JSON_CLASS = ResponseJson.class;

}
