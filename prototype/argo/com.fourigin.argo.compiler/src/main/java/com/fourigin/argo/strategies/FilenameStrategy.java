package com.fourigin.argo.strategies;

import com.fourigin.argo.models.structure.nodes.SiteNodeInfo;

public interface FilenameStrategy
{
	/**
	 * Returns the relative filename without extension. The filename does not include any path informations.
	 *
	 * @param language the language.
	 * @param info the node info.
	 * @return the filename for the given node info.
	 */
	String getFilename(String language, SiteNodeInfo info);

	/**
	 * Returns the folder. The folder does not include the filename.

     * @param language the language.
	 * @param info the node info.
	 * @return  the folder for the given node info.
	 */
	String getFolder(String language, SiteNodeInfo info);
}
