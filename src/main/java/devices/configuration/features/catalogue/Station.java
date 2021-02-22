package devices.configuration.features.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import devices.configuration.DomainEvent;
import devices.configuration.features.catalogue.location.Location;
import devices.configuration.features.catalogue.location.OpeningHours;
import devices.configuration.features.catalogue.photo.StationPhoto;
import devices.configuration.legacy.StationLocation;
import devices.configuration.legacy.StationLocationUpdated;
import devices.configuration.published.StationSnapshot;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Entity
@Data
@Accessors(chain = true)
public class Station {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true)
    private String name;
    @JsonIgnore
    private String cpo;
    @JsonIgnore
    private String lcp;

    private String physicalReference;
    private String customName;
    private Integer numberOfOutlets;
    private Integer maxNumberOfOutlets;
    private String vendor;
    private String product;
    private String productDetails;
    private String color;
    private String comment1;
    private String comment2;

    @Embedded
    private StationCapabilities capabilities;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "station_id")
    @OrderBy("ocppConnectorId")
    private List<Connector> connectors = new ArrayList<>();

    @CreationTimestamp
    private Instant addedOn;
    @UpdateTimestamp
    private Instant modifiedOn;

    @NotNull
    private String imageId;

    @Valid
    @Embedded
    private Location location;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private OpeningHours openingHours;

    public Settings getSettings() {
        if (settings != null) {
            return settings;
        } else {
            return Settings.defaultSettings();
        }
    }

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Settings settings;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "station_id")
    private List<StationPhoto> photos = new ArrayList<>();

    @JsonIgnore
    private transient Collection<DomainEvent> events = new ArrayList<>();

    String toStringCsv(String delimiter) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(delimiter);
        sb.append(physicalReference).append(delimiter);
        sb.append(connectors.size()).append(delimiter);
        sb.append(maxNumberOfOutlets).append(delimiter);
        sb.append(vendor).append(delimiter);
        sb.append(product).append(delimiter);
        sb.append(productDetails).append(delimiter);
        sb.append(color).append(delimiter);
        sb.append(comment1).append(delimiter);
        sb.append(comment2).append(delimiter);
        sb.append(capabilities.toStringCsv(delimiter));

        for (Connector connector : connectors) {
            sb.append(connector.toStringCsv(delimiter));
        }

        return sb.toString();
    }

    public Station setConnectors(List<Connector> connectors) {
        this.connectors.clear();
        if (connectors != null) {
            this.connectors.addAll(connectors);
        }
        return this;
    }

    public void apply(Station other) {
        requireNonNull(other);

        this.name = other.name;
        this.physicalReference = other.physicalReference;
        this.maxNumberOfOutlets = other.maxNumberOfOutlets;
        this.vendor = other.vendor;
        this.product = other.product;
        this.productDetails = other.productDetails;
        this.color = other.color;
        this.comment1 = other.comment1;
        this.comment2 = other.comment2;
        this.capabilities = other.capabilities;
        setConnectors(other.connectors);
    }

    public void updateOwnership(Ownership ownership) {
        if (!Objects.equals(getOwnership(), ownership)) {
            cpo = ownership.getCpo();
            lcp = ownership.getLcp();
            emitUpdated();
        }
    }

    public void updateLocation(Location location) {
        if (!Objects.equals(this.location, location)) {
            setLocation(location);
            events.add(new StationLocationUpdated(
                    name,
                    StationLocation.from(location),
                    location.isUpdate()
            ));
            emitUpdated();
        }
    }

    public void updateOpening(OpeningHours opening) {
        if (!Objects.equals(getOpeningHours(), opening)) {
            setOpeningHours(opening);
            emitUpdated();
        }
    }

    public void addPhoto(StationPhoto photo) {
        photos.add(photo);
        emitUpdated();
    }

    public void removePhotos(Predicate<StationPhoto> toRemove) {
        if (photos.removeIf(toRemove)) {
            emitUpdated();
        }
    }

    public void updateSettings(Settings settings) {
        Settings merged = getSettings().merge(settings);
        if (!Objects.equals(getSettings(), merged)) {
            setSettings(merged);
            emitUpdated();
        }
    }

    public Station emitUpdated() {
        events.addAll(StationSnapshot.updated(this));
        return this;
    }

    public Station emitDeleted() {
        events.addAll(StationSnapshot.deleted(this));
        return this;
    }

    @JsonIgnore
    public Ownership getOwnership() {
        return new Ownership(cpo, lcp);
    }

    public OpeningHours getOpeningHours() {
        return OpeningHours.alwaysOpenOrGiven(openingHours);
    }

    public Settings.Visibility getVisibility() {
        StationValidation validations = validate();
        return Settings.Visibility.of(
                validations.isValidForCharging() &&
                        validations.isValidForPublication() &&
                        settings.isPublicAccess(),
                settings.isShowOnMap()
        );
    }

    public StationValidation validate() {
        return StationValidation.validate(this);
    }

    @DomainEvents
    public Collection<DomainEvent> events() {
        return events;
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        events.clear();
    }

    public <K, V> Map<K, V> mapConnectors(Function<Connector, K> key, Function<Connector, V> value) {
        return connectors.stream().collect(Collectors.toUnmodifiableMap(key, value));
    }
}
