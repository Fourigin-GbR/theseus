package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

public interface FilenameStrategy
{
	/**
	 * Returns the relative filename without extension. The filename does not include any path informations.
	 *
	 * @param info the node info.
	 * @return the filename for the given node info.
	 */
	String getFilename(SiteNodeInfo info);

	/**
	 * Returns the folder. The folder does not include the filename.
	 * @param info the node info.
	 * @return  the folder for the given node info.
	 */
	String getFolder(SiteNodeInfo info);
}
