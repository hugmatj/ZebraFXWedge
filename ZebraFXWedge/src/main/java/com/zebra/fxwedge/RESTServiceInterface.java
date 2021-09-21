package com.zebra.fxwedge;

import android.util.Pair;

import fi.iki.elonen.NanoHTTPD;

public interface RESTServiceInterface {
    Pair<RESTServiceWebServer.EJobStatus, String> processSession(NanoHTTPD.IHTTPSession session);
}
