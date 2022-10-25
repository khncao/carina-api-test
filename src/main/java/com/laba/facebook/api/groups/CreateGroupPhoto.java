package com.laba.facebook.api.groups;

import com.laba.utils.Encryption;
import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;
import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.annotation.RequestTemplatePath;
import com.qaprosoft.carina.core.foundation.api.annotation.ResponseTemplatePath;
import com.qaprosoft.carina.core.foundation.api.annotation.SuccessfulHttpStatus;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;

@Endpoint(url = "${config.env.api_url}/${group_id}/photos", methodType = HttpMethodType.POST)
@RequestTemplatePath(path = "api/groups/photos/_post/rq.json")
@ResponseTemplatePath(path = "api/groups/photos/_post/rs.json")
@SuccessfulHttpStatus(status = HttpResponseStatusType.CREATED_201)
public class CreateGroupPhoto extends AbstractApiMethodV2 {
    public CreateGroupPhoto() {
        replaceUrlPlaceholder("group_id", Encryption.getInstance().decryptEnvProperty("group_id"));
    }
}
