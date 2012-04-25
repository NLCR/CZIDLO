package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class DigitalDocumentDTO implements Serializable {

	private static final long serialVersionUID = 4858807458088967311L;
	private Long id;
	private RegistrarDTO registrar;
	private ArchiverDTO archiver;
	private String created;
	private String modified;
	private String urn;
	private ArrayList<RegistrarScopeIdDTO> registrarScopeIdList;
	private String financed;
	private String contractNumber;
	private TechnicalMetadataDTO technicalMetadata;
	private ArrayList<DigitalInstanceDTO> instances;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RegistrarDTO getRegistrar() {
		return registrar;
	}

	public void setRegistrar(RegistrarDTO registrar) {
		this.registrar = registrar;
	}

	public ArchiverDTO getArchiver() {
		return archiver;
	}

	public void setArchiver(ArchiverDTO archiver) {
		this.archiver = archiver;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public ArrayList<RegistrarScopeIdDTO> getRegistrarScopeIdList() {
		return registrarScopeIdList;
	}

	public void setRegistrarScopeIdList(ArrayList<RegistrarScopeIdDTO> registrarScopeIdList) {
		this.registrarScopeIdList = registrarScopeIdList;
	}

	public String getFinanced() {
		return financed;
	}

	public void setFinanced(String financed) {
		this.financed = financed;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public TechnicalMetadataDTO getTechnicalMetadata() {
		return technicalMetadata;
	}

	public void setTechnicalMetadata(TechnicalMetadataDTO technicalMetadata) {
		this.technicalMetadata = technicalMetadata;
	}

	public ArrayList<DigitalInstanceDTO> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<DigitalInstanceDTO> instances) {
		this.instances = instances;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
}
