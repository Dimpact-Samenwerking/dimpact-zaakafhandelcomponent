/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 INFO.nl
 * SPDX-License-Identifier: EUPL-1.2+
 */
package nl.info.zac.healthcheck.model

import nl.info.client.zgw.ztc.model.generated.ZaakType

/**
 * 4 statustype; Intake, In behandeling, Heropend, Afgerond: met Afgerond als laatste statustypevolgnummer
 * min 1 resultaattype
 * Roltypen, omschrijving generiek: initiator en behandelaar. 1 overig roltype
 * Informatieobjecttype: e-mail
 * indien zaak besluit heeft, Besluittype
 */
class ZaaktypeInrichtingscheck(val zaaktype: ZaakType) {
    var isStatustypeIntakeAanwezig: Boolean = false
    var isStatustypeInBehandelingAanwezig: Boolean = false
    var isStatustypeHeropendAanwezig: Boolean = false
    var isStatustypeAanvullendeInformatieVereist: Boolean = false
    var isStatustypeAfgerondAanwezig: Boolean = false
    var isStatustypeAfgerondLaatsteVolgnummer: Boolean = false
    var isResultaattypeAanwezig: Boolean = false
    var aantalInitiatorroltypen: Int = 0
    var aantalBehandelaarroltypen: Int = 0
    var isRolOverigeAanwezig: Boolean = false
    var isInformatieobjecttypeEmailAanwezig: Boolean = false
    var isBesluittypeAanwezig: Boolean = false
    val resultaattypesMetVerplichtBesluit: MutableList<String?> = ArrayList<String?>()
    var isZaakafhandelParametersValide: Boolean = false
    var isBrpDoelbindingenAanwezig: Boolean = false

    fun addResultaattypesMetVerplichtBesluit(resultaattypeMetVerplichtBesluit: String?) {
        this.resultaattypesMetVerplichtBesluit.add(resultaattypeMetVerplichtBesluit)
    }

    val isValide: Boolean
        get() = this.isStatustypeIntakeAanwezig &&
            this.isStatustypeInBehandelingAanwezig &&
            this.isStatustypeHeropendAanwezig &&
            this.isStatustypeAfgerondAanwezig &&
            this.isStatustypeAfgerondLaatsteVolgnummer &&
            this.isStatustypeAanvullendeInformatieVereist &&
            this.aantalInitiatorroltypen == 1 &&
            this.aantalBehandelaarroltypen == 1 &&
            this.isRolOverigeAanwezig &&
            this.isInformatieobjecttypeEmailAanwezig &&
            this.isResultaattypeAanwezig &&
            this.isZaakafhandelParametersValide &&
            this.isBrpDoelbindingenAanwezig &&
            (resultaattypesMetVerplichtBesluit.isEmpty() || this.isBesluittypeAanwezig)
}
