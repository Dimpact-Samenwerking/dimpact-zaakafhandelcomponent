/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.mail.model

import jakarta.json.bind.annotation.JsonbProperty

class MailAdres(
    @field:JsonbProperty("Email") var email: String,
    @field:JsonbProperty("Name") var name: String?
) {
    constructor(email: String) : this(email, null)
}