package de.dominikschadow.configclient.info;

import org.springframework.hateoas.RepresentationModel;

public class ApplicationInformation extends RepresentationModel<ApplicationInformation> {
    private final String info;

    public ApplicationInformation(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
