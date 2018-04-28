package com.solace.hybrid_edge_starter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

/**
 * This creates instances of the SolConnectionFactory.
 *
 */
public class SolaceConnectionFactoryBean extends AbstractFactoryBean<SolConnectionFactory> {

	Logger logger = LogManager.getLogger(SolaceConnectionFactoryBean.class);
	
	private Integer compressionLevel;
	private String host;
	private String password;
	private String sslTrustStore;
	private String sslTrustStorePassword;
	private Boolean sslValidateCertificate;
	private String username;
	private String vpnName;
	
	public SolaceConnectionFactoryBean() {
		setSingleton(false);
	}
	
	@Override
	protected SolConnectionFactory createInstance() throws Exception {
		logger.info("Creating a Solace Connection Factory to host {}", host);
		SolConnectionFactory sol = SolJmsUtility.createConnectionFactory();
		sol.setHost(host);
		sol.setUsername(username);
		sol.setPassword(password);
		sol.setVPN(vpnName);
		
		if (compressionLevel != null) {
			sol.setCompressionLevel(compressionLevel);
		}
		
		if (sslValidateCertificate != null) {
			sol.setSSLValidateCertificate(sslValidateCertificate);
		}
		
		if (sslTrustStore != null) {
			sol.setSSLTrustStore(sslTrustStore);
		}
		
		if (sslTrustStorePassword != null) {
			sol.setSSLTrustStore(sslTrustStorePassword);
		}
		
		return sol;
	}

	public Integer getCompressionLevel() {
		return compressionLevel;
	}

	public String getHost() {
		return host;
	}

	@Override
	public Class<?> getObjectType() {
		return SolConnectionFactory.class;
	}

	public String getPassword() {
		return password;
	}

	public String getSslTrustStore() {
		return sslTrustStore;
	}

	public String getSslTrustStorePassword() {
		return sslTrustStorePassword;
	}

	public Boolean getSslValidateCertificate() {
		return sslValidateCertificate;
	}

	public String getUsername() {
		return username;
	}

	public String getVpnName() {
		return vpnName;
	}

	public void setCompressionLevel(Integer compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSslTrustStore(String sslTrustStore) {
		this.sslTrustStore = sslTrustStore;
	}

	public void setSslTrustStorePassword(String sslTrustStorePassword) {
		this.sslTrustStorePassword = sslTrustStorePassword;
	}

	public void setSslValidateCertificate(Boolean sslValidateCertificate) {
		this.sslValidateCertificate = sslValidateCertificate;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setVpnName(String vpnName) {
		this.vpnName = vpnName;
	}

}
