package com.appersiano.smartledapp.client

import java.util.UUID

/**
 * SmartLedUUID map the services and characteristics of a smart led.
 * You can access quickly to uuid by using {Service}.{Characteristic}.uuid
 */
object SmartLedUUID {

    interface IBleElement {
        val uuid: UUID
        val description: String
    }

    object DeviceInformation : IBleElement {
        override val uuid: UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
        override val description: String = "Battery Service"

        object HWRevisionString : IBleElement {
            override val uuid: UUID = UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb")
            override val description: String = "Hardware Revision String"
        }

        object FWRevisionString : IBleElement {
            override val uuid: UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
            override val description: String = "Firmware Revision String"
        }

        object SerialNumberString : IBleElement {
            override val uuid: UUID = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb")
            override val description: String = "Serial Number"
        }
    }

    object HardwareControlService : IBleElement {
        override val uuid: UUID = UUID.fromString("1d14d6ee-fd63-4fa1-bfa4-8f47b42119f0")
        override val description: String = "HardwareControlService"

        object LEDControl : IBleElement {
            override val uuid: UUID = UUID.fromString("984227f3-34fc-4045-a5d0-2c581f81a153")
            override val description: String = "LED ON/OFF"
        }

        object PIRControl : IBleElement {
            override val uuid: UUID = UUID.fromString("f7bf3564-fb6d-4e53-88a4-5e37e0326063")
            override val description: String = "PIR ON/OFF"
        }

        object CurrentTime : IBleElement {
            override val uuid: UUID = UUID.fromString("f7bf3564-fb6d-4e53-88a4-5e37e0326063")
            override val description: String = "Current Time"
        }
    }

    object LEDService : IBleElement {
        override val uuid: UUID = UUID.fromString("1d14d6ee-fd63-4fa1-bfa4-8f47b42119f0")
        override val description: String = "LEDService"

        object LEDColor : IBleElement {
            override val uuid: UUID = UUID.fromString("984227f3-34fc-4045-a5d0-2c581f81a153")
            override val description: String = "LED Color"
        }

        object LEDBrightness : IBleElement {
            override val uuid: UUID = UUID.fromString("f7bf3564-fb6d-4e53-88a4-5e37e0326063")
            override val description: String = "LED Brightness"
        }
    }

    object TimerService : IBleElement {
        override val uuid: UUID = UUID.fromString("c9ea48eb-ad9e-4d67-b570-69352fdc1078")
        override val description: String = "TrPrtService"

        object TimerOnOff : IBleElement {
            override val uuid: UUID = UUID.fromString("43583b64-c345-4f0e-ab70-5e047f527383")
            override val description: String = "TimerOnOff"
        }
    }
}

