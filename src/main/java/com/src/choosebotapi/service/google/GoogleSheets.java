package com.src.choosebotapi.service.google;

import java.io.IOException;
import java.util.List;

public interface GoogleSheets {
    List<List<Object>> readTable(GoogleConnection gc) throws IOException;
}
