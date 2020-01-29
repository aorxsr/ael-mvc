package org.ael.route;

import org.ael.annotation.RequestParam;
import org.ael.annotation.ResponseJson;

public interface RouteConstant {

    Class<RequestParam> REQUEST_PARAM_CLASS = RequestParam.class;

    Class<ResponseJson> RESPONSE_JSON_CLASS = ResponseJson.class;

}
