package com.peakinfo.common.event

import com.peakinfo.base.bean.Street

class CurrentStreetUpdateEvent(street: Street) {
    var street = street
}