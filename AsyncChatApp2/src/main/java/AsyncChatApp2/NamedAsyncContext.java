/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AsyncChatApp2;

import javax.servlet.AsyncContext;

/**
 *
 * @author adamt
 */
public class NamedAsyncContext {
    private AsyncContext ctx;
    private String name;

    public NamedAsyncContext(AsyncContext ctx, String name) {
        this.ctx = ctx;
        this.name = name;
    }
    
    public AsyncContext getCtx() {
        return ctx;
    }

    public void setCtx(AsyncContext ctx) {
        this.ctx = ctx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
