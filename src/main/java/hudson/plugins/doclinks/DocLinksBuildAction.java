package hudson.plugins.doclinks;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractItem;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import java.io.File;

/**
 * Action which publishes ducuments for each build.
 *
 * @author Lei Wang
 */
public class DocLinksBuildAction implements Action {

    private final AbstractItem project;
    public final  AbstractBuild<?, ?> build;
    private final Document document;

    public DocLinksBuildAction(final AbstractItem project, AbstractBuild<?, ?> build, final Document document) {
        this.project = project;
        this.build = build;
        this.document = document;
    }

    /**
     * for jobMain.jelly
     */
    public Document getDocument() {
        return document;
    }

    @Override
    public String getDisplayName() {
        if (document != null) {
            return document.getTitle();
        } else {
            return null;
        }
    }

    @Override
    public String getIconFileName() {
        return "clipboard.gif";
    }

    @Override
    public String getUrlName() {
        if (document != null) {
            return document.getId();
        } else {
            return null;
        }
    }

    public File getBuildDocDir() {
        if (build != null && document != null) {
            return new File(build.getRootDir(), document.getId());
        } else {
            return null;
        }
    }

    public DirectoryBrowserSupport doDynamic(final StaplerRequest req, final StaplerResponse rsp)
        throws IOException, ServletException {

        // check document
        final Document doc = document;
        if (doc == null) {
            LOGGER.warning(Messages.DocLinksAction_DocumentNotFound());
            rsp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        
        final FilePath basePath = new FilePath(getBuildDocDir());
        final DirectoryBrowserSupport dbs
            = new DirectoryBrowserSupport(this, basePath, Constants.PLUGIN_NAME, null, false);
        // set indexfile
        if (doc.getFile() != null) {
            dbs.setIndexFileName(doc.getFile());
        }

        return dbs;
    }

    private static final Logger LOGGER = Logger.getLogger(DocLinksAction.class.getName());
}
