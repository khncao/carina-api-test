package com.laba.facebook.api.groups;

import com.laba.utils.Encryption;
import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;
import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.ResponseTemplatePath;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;

@Endpoint(url = "${config.env.api_url}/${group_id}", methodType = HttpMethodType.GET)
@ResponseTemplatePath(path = "api/groups/group/_get/rs.json")
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class GetGroup extends AbstractApiMethodV2 {
    public GetGroup() {
        replaceUrlPlaceholder("group_id", Encryption.getInstance().decryptEnvProperty("group_id"));
    }
}
