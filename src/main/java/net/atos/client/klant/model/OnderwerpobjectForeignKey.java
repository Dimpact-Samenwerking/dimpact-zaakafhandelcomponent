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


public class OnderwerpobjectForeignKey {

    /**
     * Unieke (technische) identificatiecode van het onderwerpdeel.
     */
    @JsonbProperty("uuid")
    private UUID uuid;

    /**
     * De unieke URL van dit onderwerp object binnen deze API.
     */
    @JsonbProperty("url")
    private URI url;

    public OnderwerpobjectForeignKey() {
    }

    @JsonbCreator
    public OnderwerpobjectForeignKey(
            @JsonbProperty(value = "url") URI url
    ) {
        this.url = url;
    }

    /**
     * Unieke (technische) identificatiecode van het onderwerpdeel.
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

    public OnderwerpobjectForeignKey uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * De unieke URL van dit onderwerp object binnen deze API.
     * 
     * @return url
     **/
    public URI getUrl() {
        return url;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OnderwerpobjectForeignKey {\n");

        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
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