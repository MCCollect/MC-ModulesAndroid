package com.mc.mcmodules.model.classes.library

class ResFingerScan {

    var data: Data? = null


    class Data {
        var rightindex: DataFingerScanned? = null
        var leftindex: DataFingerScanned? = null

        class DataFingerScanned {
            var date: String? = null
            var identy_quality: Int? = null
            var fingerprintQuality: Int? = null
            var spoof_score: Double? = null
            var templates: Template? = null
        }

        class Template {
            var PNG: Png? = null

            class Png {
                var DEFAULT: String? = null
            }
        }

    }


}