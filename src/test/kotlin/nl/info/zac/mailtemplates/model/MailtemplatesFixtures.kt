/*
 * SPDX-FileCopyrightText: 2024 INFO.nl
 * SPDX-License-Identifier: EUPL-1.2+
 */
package nl.info.zac.mailtemplates.model

import net.atos.zac.mailtemplates.model.Mail
import nl.info.zac.mail.model.MailAdres
import nl.info.zac.mail.model.createMailAdres

@Suppress("LongParameterList")
fun createMailGegevens(
    from: MailAdres = createMailAdres(),
    to: MailAdres = createMailAdres(),
    replyTo: MailAdres = createMailAdres(),
    subject: String = "fakeSubject",
    body: String = "fakeBody",
    attachments: String? = null,
    createDocumentFromMail: Boolean = false
) = MailGegevens(
    from,
    to,
    replyTo,
    subject,
    body,
    attachments,
    createDocumentFromMail
)

@Suppress("LongParameterList")
fun createMailTemplate(
    id: Long = 1234L,
    name: String = "fakeName",
    onderwerp: String = "fakeOnderwerp",
    body: String = "fakeBody",
    mail: Mail = Mail.SIGNALERING_TAAK_OP_NAAM,
    isDefaultMailTemplate: Boolean = true
) = MailTemplate().apply {
    this.id = id
    this.mailTemplateNaam = name
    this.onderwerp = onderwerp
    this.body = body
    this.mail = mail
    this.isDefaultMailtemplate = isDefaultMailTemplate
}
