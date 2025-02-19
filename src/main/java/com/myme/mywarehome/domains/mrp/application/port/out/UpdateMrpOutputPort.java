package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;

public interface UpdateMrpOutputPort {
    void deactivatePreviousOrders();
    void orderSuccess(MrpOutput mrpOutput);
}
