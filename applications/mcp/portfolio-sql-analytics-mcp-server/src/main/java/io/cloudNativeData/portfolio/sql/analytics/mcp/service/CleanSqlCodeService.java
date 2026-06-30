package io.cloudNativeData.portfolio.sql.analytics.mcp.service;

import org.springframework.stereotype.Service;

@Service
public class CleanSqlCodeService {

    public String cleanSqlCodeBlocks(String sql) {
        if (sql == null) return "";
        return sql.replaceAll(".*```sql", "")
                .replaceAll("```.*$", "")
                .trim();
    }
}
