package com.github.nderwin.scrolling.control;

import com.github.nderwin.scrolling.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    public List<Product> findByPopularity(final String category, final String cursor, final int limit) {
        if (null == cursor || cursor.isBlank()) {
            if (null == category) {
                return find("ORDER BY viewCount DESC, id ASC").page(0, limit).list();
            } else {
                return find(
                        "category = :category ORDER BY viewCount DESC, id ASC",
                        Parameters.with("category", category)
                ).page(0, limit).list();
            }
        }

        final String[] parts = cursor.split(":");
        final int cursorViews = Integer.parseInt(parts[0]);
        final long cursorId = Long.parseLong(parts[1]);
        
        final String where = null == category
                ? """
                  WHERE (viewCount < :views)
                    OR (viewCount = :views AND id > :id)
                  ORDER BY viewCount DESC, id ASC
                  """
                : """
                  WHERE category = :category
                    AND ((viewCount < :views)
                        OR (viewCount = :views AND id > :id))
                  ORDER BY viewCount DESC, id ASC
                  """;
        
        final Parameters params = Parameters
                .with("views", cursorViews)
                .and ("id", cursorId);
        
        if (null != category) {
            params.and("category", category);
        }
        
        return find(where, params).page(0, limit).list();
    }

}
