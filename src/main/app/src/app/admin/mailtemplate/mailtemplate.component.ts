/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 INFO.nl
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { ActivatedRoute, Router } from "@angular/router";
import { TranslateService } from "@ngx-translate/core";
import { of } from "rxjs";
import { catchError } from "rxjs/operators";
import { ConfiguratieService } from "../../configuratie/configuratie.service";
import { UtilService } from "../../core/service/util.service";
import { HtmlEditorFormField } from "../../shared/material-form-builder/form-components/html-editor/html-editor-form-field";
import { HtmlEditorFormFieldBuilder } from "../../shared/material-form-builder/form-components/html-editor/html-editor-form-field-builder";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { ReadonlyFormField } from "../../shared/material-form-builder/form-components/readonly/readonly-form-field";
import { ReadonlyFormFieldBuilder } from "../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder";
import { SelectFormField } from "../../shared/material-form-builder/form-components/select/select-form-field";
import { SelectFormFieldBuilder } from "../../shared/material-form-builder/form-components/select/select-form-field-builder";
import { AbstractFormControlField } from "../../shared/material-form-builder/model/abstract-form-control-field";
import { GeneratedType } from "../../shared/utils/generated-types";
import { AdminComponent } from "../admin/admin.component";
import { MailtemplateBeheerService } from "../mailtemplate-beheer.service";
import { mailSelectList } from "../model/mail-utils";

@Component({
  templateUrl: "./mailtemplate.component.html",
  styleUrls: ["./mailtemplate.component.less"],
})
export class MailtemplateComponent
  extends AdminComponent
  implements OnInit, OnDestroy, AfterViewInit
{
  @ViewChild("sideNavContainer") sideNavContainer!: MatSidenavContainer;
  @ViewChild("menuSidenav") menuSidenav!: MatSidenav;

  fields = {
    NAAM: "mailTemplateNaam",
    MAIL: "mail",
    ONDERWERP: "onderwerp",
    BODY: "body",
    DEFAULT_MAILTEMPLATE: "defaultMailtemplate",
  };

  naamFormField?: AbstractFormControlField<string>;
  mailFormField?:
    | ReadonlyFormField
    | SelectFormField<{ label: string; value: string }>;
  onderwerpFormField?: HtmlEditorFormField;
  bodyFormField?: HtmlEditorFormField;
  defaultMailtemplateFormField?: AbstractFormControlField;

  template?: GeneratedType<"RESTMailtemplate"> | null = null;

  isLoadingResults = false;

  constructor(
    public utilService: UtilService,
    public configuratieService: ConfiguratieService,
    private mailTemplateBeheerService: MailtemplateBeheerService,
    private route: ActivatedRoute,
    private router: Router,
    private translateService: TranslateService,
  ) {
    super(utilService, configuratieService);
  }

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      this.init(data?.template ?? {});
    });
  }

  init(mailtemplate: GeneratedType<"RESTMailtemplate">) {
    this.template = mailtemplate;
    this.setupMenu("title.mailtemplate");
    this.createForm(mailtemplate);
  }

  createForm(mailtemplate: GeneratedType<"RESTMailtemplate">) {
    // const mails = this.utilService.getEnumAsSelectList("mail", Mail);

    this.naamFormField = new InputFormFieldBuilder(
      mailtemplate.mailTemplateNaam,
    )
      .id(this.fields.NAAM)
      .label(this.fields.NAAM)
      .validators(Validators.required)
      .build();
    if (mailtemplate.mail) {
      this.mailFormField = new ReadonlyFormFieldBuilder(
        this.translateService.instant("mail." + mailtemplate.mail),
      )
        .id(this.fields.MAIL)
        .label(this.fields.MAIL)
        .build();
    } else {
      this.mailFormField = new SelectFormFieldBuilder<{
        label: string;
        value: string;
      }>()
        .id(this.fields.MAIL)
        .label(this.fields.MAIL)
        .optionLabel("label")
        .options(mailSelectList())
        .settings({
          translateLabels: true,
          capitalizeFirstLetter: true,
        })
        .validators(Validators.required)
        .build();
    }
    this.onderwerpFormField = new HtmlEditorFormFieldBuilder(
      mailtemplate.onderwerp,
    )
      .id(this.fields.ONDERWERP)
      .label(this.fields.ONDERWERP)
      .variabelen(mailtemplate.variabelen ?? [])
      .emptyToolbar()
      .validators(Validators.required)
      .maxlength(100)
      .build();
    this.bodyFormField = new HtmlEditorFormFieldBuilder(mailtemplate.body)
      .id(this.fields.BODY)
      .label(this.fields.BODY)
      .variabelen(mailtemplate.variabelen ?? [])
      .validators(Validators.required)
      .build();
    this.defaultMailtemplateFormField = new InputFormFieldBuilder(
      mailtemplate.defaultMailtemplate,
    )
      .id(this.fields.DEFAULT_MAILTEMPLATE)
      .label(this.fields.DEFAULT_MAILTEMPLATE)
      .build();

    this.subscriptions$.push(
      (
        this.mailFormField.formControl as FormControl<{
          value: GeneratedType<"Mail">;
        }>
      ).valueChanges.subscribe((value) => {
        if (value) {
          this.mailTemplateBeheerService
            .ophalenVariabelenVoorMail(value.value)
            .subscribe((variabelen) => {
              if (this.onderwerpFormField) {
                this.onderwerpFormField.variabelen = variabelen;
              }
              if (this.bodyFormField) {
                this.bodyFormField.variabelen = variabelen;
              }
            });
        }
      }),
    );
  }

  ngOnDestroy() {
    this.subscriptions$.forEach((subscription) => subscription.unsubscribe());
  }

  saveMailtemplate() {
    if (!this.template) return;
    this.template.defaultMailtemplate = Boolean(
      this.defaultMailtemplateFormField?.formControl.value,
    );
    this.template.mailTemplateNaam =
      this.naamFormField?.formControl.value ?? "";
    if (!this.template.mail) {
      this.template.mail = (
        this.mailFormField?.formControl.value as { value: string }
      )?.value;
    }
    this.template.mail =
      this.template.mail ??
      (this.mailFormField?.formControl.value as { value: string })?.value;
    this.template.onderwerp = this.onderwerpFormField?.formControl.value ?? "";
    this.template.body = this.bodyFormField?.formControl.value ?? "";
    this.persistMailtemplate();
  }

  private persistMailtemplate() {
    this.mailTemplateBeheerService
      .persistMailtemplate(this.template!)
      .pipe(catchError(() => of(this.template)))
      .subscribe(() => {
        this.utilService.openSnackbar("msg.mailtemplate.opgeslagen");
        void this.router.navigate(["/admin/mailtemplates"]);
      });
  }

  cancel() {
    void this.router.navigate(["/admin/mailtemplates"]);
  }

  isInvalid() {
    return Boolean(
      this.naamFormField?.formControl.invalid ||
        this.mailFormField?.formControl.invalid ||
        this.onderwerpFormField?.formControl.invalid ||
        this.bodyFormField?.formControl.invalid,
    );
  }
}
