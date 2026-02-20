package com.github.nderwin.lean.dashboard.boundary;

import com.github.nderwin.lean.dashboard.entity.Customer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.TEXT_HTML;

@Path("/")
@RequestScoped
public class DashboardResource {
    
    @Inject
    Template dashboard;
    
    @Produces(TEXT_HTML)
    @GET
    public TemplateInstance get() {
        final List<Customer> customers = List.of(
                new Customer("C001", "Acme Corp", "contact@acme.com", "Active", "Discussed Q4 contract renewal."),
                new Customer("C002", "Globex", "info@globex.com", "Pending", "Waiting on procurement approval."),
                new Customer("C003", "Soylent Cotp", "sales@soylent.com", "Inactive", "Contract expired last month.")
        );
        
        return dashboard.data("customers", customers);
    }

}
