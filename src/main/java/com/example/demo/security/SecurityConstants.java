package com.example.demo.security;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/api/auth/**";
    public static final String GET_ALL_POSTS_URL = "/api/post/all/**";
    public static final String GET_ALL_COMMENTS_URL = "/api/comment/all/**";
    public static final String GET_ALL_IMAGES_TO_POSTS_URL = "/api/image/getImage/**";
    public static final String GET_POST_BY_ID = "/api/post/getPost/**";
    public static final String SECRET = "RainbowWithUnicorns";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 604_800_000;
}
