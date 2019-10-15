package org.ael.mvc.route;

import org.ael.mvc.annotation.RequestParam;
import org.ael.mvc.annotation.ResponseJson;

public interface RouteConstant {

    Class<RequestParam> requestParam = RequestParam.class;

    Class<ResponseJson> responseJson = ResponseJson.class;

}
