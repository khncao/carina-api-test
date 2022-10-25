package com.laba.facebook.api.groups;

import com.laba.utils.Encryption;
import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;
import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.ResponseTemplatePath;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;

@Endpoint(url = "${config.env.api_url}/${user_id}/groups", methodType = HttpMethodType.GET)
@ResponseTemplatePath(path = "api/groups/user_groups/_get/rs.json")
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class GetUserGroups extends AbstractApiMethodV2 {
    public GetUserGroups() {
        replaceUrlPlaceholder("user_id", Encryption.getInstance().decryptEnvProperty("user_id"));
    }
}
