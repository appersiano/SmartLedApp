package com.appersiano.smartledapp.client

import java.util.UUID

/**
 * SmartLedUUID map the services and characteristics of a smart led.
 * You can access quickly to uuid by using {Service}.{Characteristic}.uuid
 *
 * @see <a href="https://www.notion.so/Ble-Protocol-1badb9c3b4de4b739d4ee05b9ad5978b">Bluetooth Specification Notion </a>
 */
object SmartLedUUID {

    interface IBleElement {
        val uuid: UUID
        val description: String
    }

    object CradleSmartLightService : IBleElement {
        override val uuid: UUID = UUID.fromString("c9ea4800-ad9e-4d67-b570-69352fdc1078")
        override val description: String = "Cradle Smart Light Service"

        object LEDStatus : IBleElement {
            override val uuid: UUID = UUID.fromString("c9ea4801-ad9e-4d67-b570-69352fdc1078")
            override val description: String = "Led Status"
        }

        object LEDColor : IBleElement {
            override val uuid: UUID = UUID.fromString("c9ea4802-ad9e-4d67-b570-69352fdc1078")
            override val description: String = "Led Color"
        }

        object LEDBrightness : IBleElement {
            override val uuid: UUID = UUID.fromString("c9ea4803-ad9e-4d67-b570-69352fdc1078")
            override val description: String = "Led Brightness"
        }

        object PIRStatus : IBleElement {
            override val uuid: UUID = UUID.fromString("c9ea4804-ad9e-4d67-b570-69352fdc1078")
            override val description: String = "Pir Status"
        }

        object CurrentTime : IBleElement {
            override val uuid: UUID = UUID.fromString("c9ea4805-ad9e-4d67-b570-69352fdc1078")
            override val description: String = "Current Time"
        }

        object TimerFeature : IBleElement {
            override val uuid: UUID = UUID.fromString("c9ea4806-ad9e-4d67-b570-69352fdc1078")
            override val description: String = "Timer Feature"
        }
    }
}

