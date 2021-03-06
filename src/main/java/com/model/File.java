package com.model;

import javax.persistence.*;
import java.util.Objects;

@NamedEntityGraph(name = "file.storage", attributeNodes = @NamedAttributeNode("storage"))
@Entity
@Table(name = "FILES")
public class File {

    private long id;
    private String name;
    private String format;
    private long size;
    private Storage storage;

    public File(String name, String format, long size) {
        this.name = name;
        this.format = format;
        this.size = size;
    }

    public File() {
    }

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "FILE_SEQUENCE", sequenceName = "FILE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_SEQUENCE")
    public long getId() {
        return id;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @Column(name = "FORMAT")
    public String getFormat() {
        return format;
    }

    @Column(name = "FILE_SIZE")
    public long getSize() {
        return size;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORAGE_ID")
    public Storage getStorage() {
        return storage;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", size=" + size +
                ", storage=" + storage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id == file.id &&
                name.equals(file.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
