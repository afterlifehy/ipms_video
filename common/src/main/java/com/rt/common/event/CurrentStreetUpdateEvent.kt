package com.rt.common.event

import com.rt.base.bean.Street

class CurrentStreetUpdateEvent(street: Street) {
    var street = street
}