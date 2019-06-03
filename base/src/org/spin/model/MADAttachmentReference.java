/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2015 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpya.com                                  *
 *****************************************************************************/
package org.spin.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * @author Yamel Senih, ySenih@erpya.com, ERPCyA http://www.erpya.com
 *		Add Support to external storage for attachment
 */
public class MADAttachmentReference extends X_AD_AttachmentReference {

	public MADAttachmentReference(Properties ctx, int AD_AttachmentReference_ID, String trxName) {
		super(ctx, AD_AttachmentReference_ID, trxName);
	}

	public MADAttachmentReference(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/** Static Cache */
	private static CCache<Integer, MADAttachmentReference> attachmentReferenceCacheIds = new CCache<Integer, MADAttachmentReference>(Table_Name, 30);
	/** Static Cache */
	private static CCache<String, MADAttachmentReference> attachmentReferenceCacheUuids = new CCache<String, MADAttachmentReference>(Table_Name, 30);
	/** Static Cache */
	private static CCache<String, MADAttachmentReference> attachmentReferenceCacheExternCall = new CCache<String, MADAttachmentReference>(Table_Name, 30);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3160093764545078785L;
	
	/**
	 * Get converted File name for external storage
	 * @return
	 */
	public String getValidFileName() {
		String uuid = get_UUID();
		if(Util.isEmpty(uuid)) {
			throw new AdempiereException("@UUID@ @NotFound@");
		}
		return (uuid + "-" + getFileName())
				.replaceAll("[+^:&áàäéèëíìïóòöúùñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ$()*#/]", "")
				.replaceAll(" ", "-");
	}
	
	/**
	 * Get/Load Attachment Reference [CACHED]
	 * @param ctx context
	 * @param attachmentReferenceId
	 * @param trxName
	 * @return activity or null
	 */
	public static MADAttachmentReference getById(Properties ctx, int attachmentReferenceId, String trxName) {
		if (attachmentReferenceId <= 0)
			return null;

		MADAttachmentReference attachmentReference = attachmentReferenceCacheIds.get(attachmentReferenceId);
		if (attachmentReference != null && attachmentReference.get_ID() > 0)
			return attachmentReference;

		attachmentReference = new Query(ctx , Table_Name , COLUMNNAME_AD_AttachmentReference_ID + "=?" , trxName)
				.setClient_ID()
				.setParameters(attachmentReferenceId)
				.first();
		if (attachmentReference != null && attachmentReference.get_ID() > 0) {
			String key = attachmentReference.getUUID();
			attachmentReferenceCacheUuids.put(key, attachmentReference);
			attachmentReferenceCacheIds.put(attachmentReference.get_ID(), attachmentReference);
		}
		return attachmentReference;
	}
	
	/**
	 * Get/Load Attachment Reference from image [CACHED]
	 * @param ctx context
	 * @param fileHandlerId
	 * @param imageId
	 * @param trxName
	 * @return activity or null
	 */
	public static MADAttachmentReference getByImageId(Properties ctx, int fileHandlerId, int imageId, String trxName) {
		if (imageId <= 0)
			return null;
		String key = "Image#" + fileHandlerId + "|" + imageId;
		MADAttachmentReference attachmentReference = attachmentReferenceCacheExternCall.get(key);
		if (attachmentReference != null && attachmentReference.get_ID() > 0)
			return attachmentReference;

		attachmentReference = new Query(ctx , Table_Name , COLUMNNAME_FileHandler_ID + " = ? "
				+ "AND " + COLUMNNAME_AD_Image_ID + " = ?" , trxName)
				.setClient_ID()
				.setParameters(fileHandlerId, imageId)
				.first();
		if (attachmentReference != null && attachmentReference.get_ID() > 0) {
			attachmentReferenceCacheUuids.put(attachmentReference.getUUID(), attachmentReference);
			attachmentReferenceCacheIds.put(attachmentReference.get_ID(), attachmentReference);
			attachmentReferenceCacheExternCall.put(key, attachmentReference);
		}
		return attachmentReference;
	}
	
	/**
	 * Get/Load Attachment Reference from archive [CACHED]
	 * @param ctx context
	 * @param fileHandlerId
	 * @param archiveId
	 * @param trxName
	 * @return activity or null
	 */
	public static MADAttachmentReference getByArchiveId(Properties ctx, int fileHandlerId, int archiveId, String trxName) {
		if (archiveId <= 0)
			return null;
		String key = "Archive#" + fileHandlerId + "|" + archiveId;
		MADAttachmentReference attachmentReference = attachmentReferenceCacheExternCall.get(key);
		if (attachmentReference != null && attachmentReference.get_ID() > 0)
			return attachmentReference;

		attachmentReference = new Query(ctx , Table_Name , COLUMNNAME_FileHandler_ID + " = ? "
				+ "AND " + COLUMNNAME_AD_Archive_ID + " = ?" , trxName)
				.setClient_ID()
				.setParameters(fileHandlerId, archiveId)
				.first();
		if (attachmentReference != null && attachmentReference.get_ID() > 0) {
			attachmentReferenceCacheUuids.put(attachmentReference.getUUID(), attachmentReference);
			attachmentReferenceCacheIds.put(attachmentReference.get_ID(), attachmentReference);
			attachmentReferenceCacheExternCall.put(key, attachmentReference);
		}
		return attachmentReference;
	}
	
	/**
	 * Get/Load Attachment Reference from Attachment [CACHED]
	 * @param ctx context
	 * @param fileHandlerId
	 * @param attachmentId
	 * @param fileName
	 * @param trxName
	 * @return activity or null
	 */
	public static MADAttachmentReference getByAttachmentId(Properties ctx, int fileHandlerId, int attachmentId, String fileName, String trxName) {
		if (attachmentId <= 0)
			return null;
		String key = "Attachment#" + fileHandlerId + "|" + attachmentId + "|" + fileName;
		MADAttachmentReference attachmentReference = attachmentReferenceCacheExternCall.get(key);
		if (attachmentReference != null && attachmentReference.get_ID() > 0)
			return attachmentReference;

		attachmentReference = new Query(ctx , Table_Name , COLUMNNAME_FileHandler_ID + " = ? "
				+ "AND " + COLUMNNAME_AD_Archive_ID + " = ? "
				+ "AND " + COLUMNNAME_FileName + " = ?", trxName)
				.setClient_ID()
				.setParameters(fileHandlerId, attachmentId, fileName)
				.first();
		if (attachmentReference != null && attachmentReference.get_ID() > 0) {
			attachmentReferenceCacheUuids.put(attachmentReference.getUUID(), attachmentReference);
			attachmentReferenceCacheIds.put(attachmentReference.get_ID(), attachmentReference);
			attachmentReferenceCacheExternCall.put(key, attachmentReference);
		}
		return attachmentReference;
	}

	/**
	 * get attachment reference By Value [CACHED]
	 * @param ctx
	 * @param attachmentReferenceUuid
	 * @param trxName
	 * @return
	 */
	public static MADAttachmentReference getByUuid(Properties ctx, String attachmentReferenceUuid, String trxName) {
		if (attachmentReferenceUuid == null)
			return null;
		if (attachmentReferenceCacheUuids.size() == 0 )
			getAll(ctx, true, trxName);

		String key = attachmentReferenceUuid;
		MADAttachmentReference attachmentReference = attachmentReferenceCacheUuids.get(key);
		if (attachmentReference != null && attachmentReference.get_ID() > 0 )
			return attachmentReference;

		attachmentReference =  new Query(ctx, Table_Name , COLUMNNAME_UUID +  "=?", trxName)
				.setClient_ID()
				.setParameters(attachmentReferenceUuid)
				.first();

		if (attachmentReference != null && attachmentReference.get_ID() > 0) {
			attachmentReferenceCacheUuids.put(key, attachmentReference);
			attachmentReferenceCacheIds.put(attachmentReference.get_ID() , attachmentReference);
		}
		return attachmentReference;
	}

	/**
	 * Get All Activity
	 * @param ctx
	 * @param resetCache
	 * @param trxName
	 * @return
	 */
	public static List<MADAttachmentReference> getAll(Properties ctx, boolean resetCache, String trxName) {
		List<MADAttachmentReference> attachmentReferenceList;
		if (resetCache || attachmentReferenceCacheIds.size() > 0 ) {
			attachmentReferenceList = new Query(Env.getCtx(), Table_Name, null , trxName)
					.setClient_ID()
					.list();
			attachmentReferenceList.stream().forEach(attachmentReference -> {
				String key = attachmentReference.getUUID();
				attachmentReferenceCacheIds.put(attachmentReference.getAD_AttachmentReference_ID(), attachmentReference);
				attachmentReferenceCacheUuids.put(key, attachmentReference);
			});
			return attachmentReferenceList;
		}
		attachmentReferenceList = attachmentReferenceCacheIds.entrySet().stream()
				.map(activity -> activity.getValue())
				.collect(Collectors.toList());
		return  attachmentReferenceList;
	}

	@Override
	public String toString() {
		return "MADAttachmentReference [getFileName()=" + getFileName() + ", getUUID()=" + getUUID() + "]";
	}
}
