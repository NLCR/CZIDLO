package cz.nkp.urnnbn.client;

import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;

public interface DigitalInstanceRefreshable {
    public void refresh(DigitalInstanceDTO di);
}
