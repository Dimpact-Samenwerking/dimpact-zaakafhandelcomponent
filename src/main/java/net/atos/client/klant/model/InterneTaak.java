/**
 * klantinteracties
 * Description WIP.
 *
 * The version of the OpenAPI document: 0.0.3
 * Contact: standaarden.ondersteuning@vng.nl
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.klant.model;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;


public class InterneTaak {

    /**
     * Unieke (technische) identificatiecode van de interne taak.
     */
    @JsonbProperty("uuid")
    private UUID uuid;

    /**
     * De unieke URL van deze interne taak binnen deze API.
     */
    @JsonbProperty("url")
    private URI url;

    /**
     * Uniek identificerend nummer dat tijdens communicatie tussen mensen kan worden gebruikt om de specifieke interne taak aan te duiden.
     */
    @JsonbProperty("nummer")
    private String nummer;

    /**
     * Handeling die moet worden uitgevoerd om de taak af te ronden.
     */
    @JsonbProperty("gevraagdeHandeling")
    private String gevraagdeHandeling;

    /**
     * Klantcontact dat leidde tot een interne taak.
     */
    @JsonbProperty("aanleidinggevendKlantcontact")
    private KlantcontactForeignKey aanleidinggevendKlantcontact;

    /**
     * Actor die een interne taak toegewezen kreeg.
     */
    @JsonbProperty("toegewezenAanActor")
    private ActorForeignKey toegewezenAanActor;

    /**
     * Toelichting die, aanvullend bij de inhoud van het klantcontact dat aanleiding gaf tot de taak en de gevraagde handeling, bijdraagt
     * aan het kunnen afhandelen van de taak.
     */
    @JsonbProperty("toelichting")
    private String toelichting;

    /**
     * Aanduiding van de vordering bij afhandeling van de interne taak.
     */
    @JsonbProperty("status")
    private StatusEnum status;

    /**
     * Datum en tijdstip waarop de interne taak aan een actor werd toegewezen.
     */
    @JsonbProperty("toegewezenOp")
    private OffsetDateTime toegewezenOp;

    /**
     * Datum en tijdstip wanneer de interne taak was afgehandeld: EXPERIMENTEEL.
     */
    @JsonbProperty("afgehandeldOp")
    private OffsetDateTime afgehandeldOp;

    public InterneTaak() {
    }

    @JsonbCreator
    public InterneTaak(
            @JsonbProperty(value = "uuid") UUID uuid,
            @JsonbProperty(value = "url") URI url,
            @JsonbProperty(value = "toegewezenOp") OffsetDateTime toegewezenOp
    ) {
        this.uuid = uuid;
        this.url = url;
        this.toegewezenOp = toegewezenOp;
    }

    /**
     * Unieke (technische) identificatiecode van de interne taak.
     * 
     * @return uuid
     **/
    public UUID getUuid() {
        return uuid;
    }


    /**
     * De unieke URL van deze interne taak binnen deze API.
     * 
     * @return url
     **/
    public URI getUrl() {
        return url;
    }


    /**
     * Uniek identificerend nummer dat tijdens communicatie tussen mensen kan worden gebruikt om de specifieke interne taak aan te duiden.
     * 
     * @return nummer
     **/
    public String getNummer() {
        return nummer;
    }

    /**
     * Set nummer
     */
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public InterneTaak nummer(String nummer) {
        this.nummer = nummer;
        return this;
    }

    /**
     * Handeling die moet worden uitgevoerd om de taak af te ronden.
     * 
     * @return gevraagdeHandeling
     **/
    public String getGevraagdeHandeling() {
        return gevraagdeHandeling;
    }

    /**
     * Set gevraagdeHandeling
     */
    public void setGevraagdeHandeling(String gevraagdeHandeling) {
        this.gevraagdeHandeling = gevraagdeHandeling;
    }

    public InterneTaak gevraagdeHandeling(String gevraagdeHandeling) {
        this.gevraagdeHandeling = gevraagdeHandeling;
        return this;
    }

    /**
     * Klantcontact dat leidde tot een interne taak.
     * 
     * @return aanleidinggevendKlantcontact
     **/
    public KlantcontactForeignKey getAanleidinggevendKlantcontact() {
        return aanleidinggevendKlantcontact;
    }

    /**
     * Set aanleidinggevendKlantcontact
     */
    public void setAanleidinggevendKlantcontact(KlantcontactForeignKey aanleidinggevendKlantcontact) {
        this.aanleidinggevendKlantcontact = aanleidinggevendKlantcontact;
    }

    public InterneTaak aanleidinggevendKlantcontact(KlantcontactForeignKey aanleidinggevendKlantcontact) {
        this.aanleidinggevendKlantcontact = aanleidinggevendKlantcontact;
        return this;
    }

    /**
     * Actor die een interne taak toegewezen kreeg.
     * 
     * @return toegewezenAanActor
     **/
    public ActorForeignKey getToegewezenAanActor() {
        return toegewezenAanActor;
    }

    /**
     * Set toegewezenAanActor
     */
    public void setToegewezenAanActor(ActorForeignKey toegewezenAanActor) {
        this.toegewezenAanActor = toegewezenAanActor;
    }

    public InterneTaak toegewezenAanActor(ActorForeignKey toegewezenAanActor) {
        this.toegewezenAanActor = toegewezenAanActor;
        return this;
    }

    /**
     * Toelichting die, aanvullend bij de inhoud van het klantcontact dat aanleiding gaf tot de taak en de gevraagde handeling, bijdraagt
     * aan het kunnen afhandelen van de taak.
     * 
     * @return toelichting
     **/
    public String getToelichting() {
        return toelichting;
    }

    /**
     * Set toelichting
     */
    public void setToelichting(String toelichting) {
        this.toelichting = toelichting;
    }

    public InterneTaak toelichting(String toelichting) {
        this.toelichting = toelichting;
        return this;
    }

    /**
     * Aanduiding van de vordering bij afhandeling van de interne taak.
     * 
     * @return status
     **/
    public StatusEnum getStatus() {
        return status;
    }

    /**
     * Set status
     */
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public InterneTaak status(StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Datum en tijdstip waarop de interne taak aan een actor werd toegewezen.
     * 
     * @return toegewezenOp
     **/
    public OffsetDateTime getToegewezenOp() {
        return toegewezenOp;
    }


    /**
     * Datum en tijdstip wanneer de interne taak was afgehandeld: EXPERIMENTEEL.
     * 
     * @return afgehandeldOp
     **/
    public OffsetDateTime getAfgehandeldOp() {
        return afgehandeldOp;
    }

    /**
     * Set afgehandeldOp
     */
    public void setAfgehandeldOp(OffsetDateTime afgehandeldOp) {
        this.afgehandeldOp = afgehandeldOp;
    }

    public InterneTaak afgehandeldOp(OffsetDateTime afgehandeldOp) {
        this.afgehandeldOp = afgehandeldOp;
        return this;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InterneTaak {\n");

        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    nummer: ").append(toIndentedString(nummer)).append("\n");
        sb.append("    gevraagdeHandeling: ").append(toIndentedString(gevraagdeHandeling)).append("\n");
        sb.append("    aanleidinggevendKlantcontact: ").append(toIndentedString(aanleidinggevendKlantcontact)).append("\n");
        sb.append("    toegewezenAanActor: ").append(toIndentedString(toegewezenAanActor)).append("\n");
        sb.append("    toelichting: ").append(toIndentedString(toelichting)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    toegewezenOp: ").append(toIndentedString(toegewezenOp)).append("\n");
        sb.append("    afgehandeldOp: ").append(toIndentedString(afgehandeldOp)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}