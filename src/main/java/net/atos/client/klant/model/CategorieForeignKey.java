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
import java.util.UUID;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Let op: Dit attribuut is EXPERIMENTEEL.
 */

public class CategorieForeignKey {

    /**
     * Unieke (technische) identificatiecode van de Categorie.
     */
    @JsonbProperty("uuid")
    private UUID uuid;

    /**
     * De unieke URL van deze categorie binnen deze API.
     */
    @JsonbProperty("url")
    private URI url;

    /**
     * Naam van de categorie.
     */
    @JsonbProperty("naam")
    private String naam;

    public CategorieForeignKey() {
    }

    @JsonbCreator
    public CategorieForeignKey(
            @JsonbProperty(value = "url") URI url,
            @JsonbProperty(value = "naam") String naam
    ) {
        this.url = url;
        this.naam = naam;
    }

    /**
     * Unieke (technische) identificatiecode van de Categorie.
     * 
     * @return uuid
     **/
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Set uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public CategorieForeignKey uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * De unieke URL van deze categorie binnen deze API.
     * 
     * @return url
     **/
    public URI getUrl() {
        return url;
    }


    /**
     * Naam van de categorie.
     * 
     * @return naam
     **/
    public String getNaam() {
        return naam;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CategorieForeignKey {\n");

        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    naam: ").append(toIndentedString(naam)).append("\n");
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