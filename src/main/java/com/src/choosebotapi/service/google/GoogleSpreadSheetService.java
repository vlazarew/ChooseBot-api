package com.src.choosebotapi.service.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@PropertySource("classpath:google.properties")
public class GoogleSpreadSheetService implements GoogleSheets {

//    @Value("${google.app.name}")
    String appName = "ChooseEat";

//    @Value("${google.spreadsheet.id}")
    String spreadsheetId = "1WCGBVdYZ_C2IQSNLeRW65XSj46E_eLMlhU4S2ToOsn0";

//    @Value("${google.spreadsheet.sheet.name}")
    String sheetName = "Ответы на форму (1)";

    @Override
    public List<List<Object>> readTable(GoogleConnection connection) throws IOException {
        Sheets service = getSheetsService(connection);
        return readTable(service, spreadsheetId, sheetName);
    }

    private Sheets getSheetsService(GoogleConnection gc) throws IOException {
        return new Sheets.Builder(Global.HTTP_TRANSPORT, Global.JSON_FACTORY, gc.getCredentials())
                .setApplicationName(appName).build();
    }

    private List<List<Object>> readTable(Sheets service, String spreadsheetId, String sheetName) throws IOException {
        ValueRange table = service.spreadsheets().values().get(spreadsheetId, sheetName)
                .execute();

        List<List<Object>> values = table.getValues();
        printTable(values);

        return values;
    }

    private void printTable(List<List<Object>> values) {
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
            System.out.println("read data");
            for (List<Object> row : values) {
                for (int c = 0; c < row.size(); c++) {
                    System.out.printf("%s ", row.get(c));
                }
                System.out.println();
            }
        }
    }
}
