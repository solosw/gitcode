package com.solosw.codelab.config.server;

import com.solosw.codelab.entity.po.House;
import com.solosw.codelab.entity.po.HouseRight;
import com.solosw.codelab.entity.po.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jgit.http.server.resolver.DefaultReceivePackFactory;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

public class MyReceivePackFactory implements ReceivePackFactory<HttpServletRequest> {


    static GitPersmionHelper gitPersmionHelper;

    public MyReceivePackFactory(GitPersmionHelper gitPersmionHelper) {
        MyReceivePackFactory. gitPersmionHelper = gitPersmionHelper;
    }

    public ReceivePack create(HttpServletRequest req, Repository db) throws ServiceNotEnabledException, ServiceNotAuthorizedException {

     House   house= (House) req.getAttribute("my_house");
     HouseRight houseRight= (HouseRight) req.getAttribute("house_right");
     Users u=(Users) req.getAttribute("my_users");
        MyReceivePackFactory.ServiceConfig cfg = (MyReceivePackFactory.ServiceConfig)db.getConfig().get(MyReceivePackFactory.ServiceConfig::new);
        if (cfg.set) {
            if (!cfg.enabled) {
                throw new ServiceNotEnabledException();
            } else {
                if (u == null ) {
                    throw new ServiceNotEnabledException();
                }

                return createFor(req, db, u,house,houseRight);
            }
        } else if (u != null ) {
            return createFor(req, db, u,house,houseRight);
        } else {
            throw new ServiceNotAuthorizedException();
        }
    }

    private static ReceivePack createFor(HttpServletRequest req, Repository db,  Users u, House house,HouseRight houseRight) {
        ReceivePack rp = new ReceivePack(db);
        rp.setRefLogIdent(toPersonIdent(req, u.getName()));
        rp.setPreReceiveHook(new MyPreRecieveHook(house,u,houseRight,gitPersmionHelper));
        return rp;
    }

    private static PersonIdent toPersonIdent(HttpServletRequest req, String user) {
        return new PersonIdent(user, user + "@" + req.getRemoteHost());
    }

    private static class ServiceConfig {
        final boolean set;
        final boolean enabled;

        ServiceConfig(Config cfg) {
            this.set = cfg.getString("http", (String)null, "receivepack") != null;
            this.enabled = cfg.getBoolean("http", "receivepack", false);
        }
    }

}
