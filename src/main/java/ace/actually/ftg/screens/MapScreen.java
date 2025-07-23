package ace.actually.ftg.screens;

import com.miir.atlas.Atlas;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.Resource;
import net.minecraft.server.command.PlaceCommand;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MapScreen extends Screen {
    private ButtonWidget select1;
    private ButtonWidget select2;
    private ButtonWidget select3;
    private ButtonWidget select4;
    int xOffset = 0;
    int yOffset = 0;

    public MapScreen(Text title) {
        super(title);
    }


    @Override
    protected void init() {
        super.init();
        select1=this.addDrawableChild(ButtonWidget.builder(Text.of("<"),f->buttonPresses(0))
                .dimensions((width/2)-120,120,120,20).build());
        select2=this.addDrawableChild(ButtonWidget.builder(Text.of(">"),f->buttonPresses(1))
                .dimensions((width/2),120,120,20).build());
        select3=this.addDrawableChild(ButtonWidget.builder(Text.of("\\/"),f->buttonPresses(2))
                .dimensions((width/2)-120,140,120,20).build());
        select4=this.addDrawableChild(ButtonWidget.builder(Text.of("/\\"),f->buttonPresses(3))
                .dimensions((width/2),140,120,20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.drawTexture(,xOffset,yOffset,0,0,15032,7424);
        super.render(context, mouseX, mouseY, delta);
    }


    private void buttonPresses(int v)
    {
        switch (v)
        {
            case 0 -> xOffset--;
            case 1 -> xOffset++;
            case 2 -> yOffset--;
            case 3 -> yOffset++;
        }
    }
}
