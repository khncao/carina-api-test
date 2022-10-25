package com.laba.facebook.groups;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.hamcrest.Matchers;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.ArraySizeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.laba.facebook.api.groups.CreateGroupPhoto;
import com.laba.facebook.api.groups.CreateGroupPost;
import com.laba.facebook.api.groups.GetGroup;
import com.laba.facebook.api.groups.GetGroupPosts;
import com.laba.facebook.api.groups.GetUserGroups;
import com.qaprosoft.apitools.validation.JsonCompareKeywords;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.api.AbstractApiMethodV2;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;
import com.qaprosoft.carina.core.foundation.utils.ownership.MethodOwner;

import io.restassured.response.Response;

public class GroupsAPITest implements IAbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static Properties localProperties;

    @BeforeClass
    public void beforeClass() {
        try {
            localProperties = Resources.getResourceAsProperties("local.properties");
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetUserGroups() {
        GetUserGroups api = new GetUserGroups();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        testGetApi(api, HttpResponseStatusType.OK_200);
        api.validateResponse(JSONCompareMode.STRICT, JsonCompareKeywords.ARRAY_CONTAINS.getKey() + "data");
        api.validateResponseAgainstSchema(String.format("api/groups/user_groups/_get/rs.schema"));
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetUserGroupsInvalidAccessTokenError() {
        GetUserGroups api = new GetUserGroups();
        api.addUrlParameter("access_token", "123");
        Response r = testGetApi(api, HttpResponseStatusType.BAD_REQUEST_400);
        Assert.assertEquals(r.getBody().jsonPath().getInt("error.code"), 190, "Expected Error 190: Invalid OAuth 2.0 token");
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetUserGroupsInvalidParamError() {
        GetUserGroups api = new GetUserGroups();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        api.addUrlParameter("admin_only", "123");
        Response r = testGetApi(api, HttpResponseStatusType.BAD_REQUEST_400);
        Assert.assertEquals(r.getBody().jsonPath().getInt("error.code"), 100, "Expected Error 100: Invalid parameter");
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetUserGroupsAdminOnly() {
        GetUserGroups api = new GetUserGroups();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        api.addUrlParameter("admin_only", "true");
        Response r = testGetApi(api, HttpResponseStatusType.OK_200);
        api.validateResponse(JSONCompareMode.LENIENT, JsonCompareKeywords.ARRAY_CONTAINS.getKey() + "data");
        api.validateResponseAgainstSchema(String.format("api/groups/user_groups/_get/rs.schema"));
        JSONAssert.assertEquals("{data:[1]}", r.getBody().asString(),
                new ArraySizeComparator(JSONCompareMode.LENIENT));
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetGroup() {
        GetGroup api = new GetGroup();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        testGetApi(api, HttpResponseStatusType.OK_200);
        // TODO(khncao): check if can validate json field to enum(privacy)
        api.validateResponse(JSONCompareMode.STRICT, JsonCompareKeywords.ARRAY_CONTAINS.getKey());
        api.validateResponseAgainstSchema(String.format("api/groups/group/_get/rs.schema"));
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetGroupPosts() {
        GetGroupPosts api = new GetGroupPosts();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        testGetApi(api, HttpResponseStatusType.OK_200);
        api.validateResponse(JSONCompareMode.STRICT, JsonCompareKeywords.ARRAY_CONTAINS.getKey() + "data");
        api.validateResponseAgainstSchema(String.format("api/groups/posts/_get/rs.schema"));
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testGetGroupPostsUntilTimeFilter() {
        GetGroupPosts api = new GetGroupPosts();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        api.addUrlParameter("until", "2022-10-22");
        Response r = testGetApi(api, HttpResponseStatusType.OK_200);
        api.validateResponse(JSONCompareMode.LENIENT, JsonCompareKeywords.ARRAY_CONTAINS.getKey() + "data");
        api.validateResponseAgainstSchema(String.format("api/groups/posts/_get/rs.schema"));
        // TODO(khncao): shouldn't use hardcoded posts and result count; broken if any deleted
            // instead consider series of create/delete and get to validate since/until
        JSONAssert.assertEquals("{data:[6]}", r.getBody().asString(),
        new ArraySizeComparator(JSONCompareMode.LENIENT));
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testCreateGroupPost() {
        CreateGroupPost api = new CreateGroupPost();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        api.addUrlParameter("message", "Test post a b c");
        api.expectResponseStatus(HttpResponseStatusType.OK_200);
        api.callAPI();
        api.validateResponse(JSONCompareMode.STRICT);
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testCreateGroupPhoto() {
        CreateGroupPhoto api = new CreateGroupPhoto();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        // api.addUrlParameter("url", "https://pbs.twimg.com/profile_images/737359467742912512/t_pzvyZZ_400x400.jpg");
        api.setProperties("api/groups/photos/photo.properties");
        api.expectResponseStatus(HttpResponseStatusType.OK_200);
        api.callAPI();
        api.validateResponse(JSONCompareMode.STRICT);
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testCreateGroupPhotoInvalidUrl() {
        CreateGroupPhoto api = new CreateGroupPhoto();
        api.addUrlParameter("access_token", localProperties.getProperty("access_token"));
        api.addUrlParameter("url", "123");
        api.expectResponseStatus(HttpResponseStatusType.BAD_REQUEST_400);
        Response r = api.callAPI();
        Assert.assertEquals(r.getBody().jsonPath().getInt("error.code"), 324, "Expected Error 324: Missing or invalid image file");
    }

    private Response testGetApi(AbstractApiMethodV2 api, HttpResponseStatusType expectedStatus) {
        return testGetApi(api, expectedStatus.getCode(), expectedStatus.getMessage());
    }

    private Response testGetApi(AbstractApiMethodV2 api, int expectedStatusCode, String expectedStatusLine) {
        LOGGER.debug("\n" + api.getMethodPath() + "\n");
        api.getRequest().expect().statusCode(expectedStatusCode);
        api.getRequest().expect().statusLine(Matchers.containsString(expectedStatusLine));
        Response r = api.callAPI();
        return r;
    }
}
