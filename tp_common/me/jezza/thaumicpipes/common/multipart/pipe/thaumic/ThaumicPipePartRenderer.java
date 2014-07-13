package me.jezza.thaumicpipes.common.multipart.pipe.thaumic;

import static org.lwjgl.opengl.GL11.*;

import me.jezza.thaumicpipes.client.RenderUtils;
import me.jezza.thaumicpipes.client.core.NodeState;
import me.jezza.thaumicpipes.client.model.ModelJarConnection;
import me.jezza.thaumicpipes.client.model.ModelPipeExtension;
import me.jezza.thaumicpipes.client.model.ModelThaumicPipe;
import me.jezza.thaumicpipes.common.interfaces.IPartRenderer;
import me.jezza.thaumicpipes.common.lib.TextureMaps;
import me.jezza.thaumicpipes.common.multipart.pipe.PipePartAbstract;
import me.jezza.thaumicpipes.common.transport.ArmState;
import me.jezza.thaumicpipes.common.transport.connection.ConnectionType;
import net.minecraftforge.common.util.ForgeDirection;

public class ThaumicPipePartRenderer implements IPartRenderer {

    ModelThaumicPipe modelThaumicPipe;
    ModelJarConnection modelJarConnection;
    ModelPipeExtension modelPipeExtension;

    public ThaumicPipePartRenderer() {
        modelThaumicPipe = new ModelThaumicPipe();
        modelJarConnection = new ModelJarConnection();
        modelPipeExtension = new ModelPipeExtension();
    }

    public void render(ThaumicPipePart pipe, double x, double y, double z, float tick) {
        glPushMatrix();

        glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);

        float scale = 0.3850F;

        glScalef(scale, scale, scale);

        ArmState[] armSet = pipe.getArmStateArray();

        if (armSet == null)
            return;

        for (int index = 0; index < armSet.length; index++) {
            ArmState currentState = armSet[index];
            if (currentState == null || !currentState.isValid())
                continue;

            renderArm(currentState, index + 1);
        }

        renderNodeState(pipe.getNodeState());
        glPopMatrix();
    }

    private void renderNodeState(NodeState nodeState) {
        if (nodeState == null)
            return;

        if (nodeState.isNode())
            renderNode(nodeState.isBigNode());
        else
            renderNodeReplacement(nodeState.getDirection());
    }

    private void renderNodeReplacement(ForgeDirection flag) {
        glPushMatrix();
        RenderUtils.bindBorderlessTexture();
        switch (flag) {
            case DOWN:
                glRotatef(90, 1.0F, 0.0F, 0.0F);
            case NORTH:
                glRotatef(90, 0.0F, 1.0F, 0.0F);
            case WEST:
                glTranslatef(-0.2F, 0.0F, 0.0F);
                modelPipeExtension.renderAll();
                glTranslatef(0.2F, 0.0F, 0.0F);
                modelPipeExtension.renderAll();
                glTranslatef(0.2F, 0.0F, 0.0F);
                modelPipeExtension.renderAll();
                break;
            default:
                break;
        }
        glPopMatrix();
    }

    private void renderArm(ArmState armState, int index) {
        glPushMatrix();
        RenderUtils.bindPipeTexture();

        modelThaumicPipe.renderArm(index);

        if (armState.getType().isCustomRender())
            processPostArmRender(armState, index);

        glPopMatrix();
    }

    private void processPostArmRender(ArmState currentState, int index) {
        ForgeDirection currentDir = currentState.getDirection();
        ConnectionType type = currentState.getType();

        float xDisplace = currentDir.offsetX;
        float yDisplace = currentDir.offsetY;
        float zDisplace = currentDir.offsetZ;

        if (type.isJar()) {
            glPushMatrix();
            glTranslatef(xDisplace, yDisplace, zDisplace);

            if (currentDir != ForgeDirection.UP) {
                glPushMatrix();

                float distance = 0.4F;
                glTranslatef(xDisplace * distance, yDisplace * distance, zDisplace * distance);

                glRotatef(90F, xDisplace, zDisplace, yDisplace);

                if (currentDir == ForgeDirection.NORTH)
                    glRotatef(180F, 0.0F, 0.0F, 1.0F);

                if (currentDir == ForgeDirection.DOWN) {
                    glRotatef(180, 0.0F, 1.0F, 0.0F);
                    RenderUtils.bindBorderlessTexture();
                    modelPipeExtension.renderAll();

                    distance = 0.20F;
                    glTranslatef(yDisplace * distance, zDisplace * distance, xDisplace * distance);
                }
                RenderUtils.bindBorderedTexture();
                modelPipeExtension.renderAll();

                glPopMatrix();
            }

            if (currentDir != ForgeDirection.DOWN) {
                glPushMatrix();
                RenderUtils.bindTexture(TextureMaps.JAR_CONNECTION);

                float distance = 0.173F;
                glTranslatef(currentDir.offsetX * distance, currentDir.offsetY * distance, currentDir.offsetZ * distance);

                if (currentDir != ForgeDirection.UP) {
                    float secondaryTranslate = 0.455F;

                    glTranslatef(currentDir.offsetX * secondaryTranslate, 0.0F, currentDir.offsetZ * secondaryTranslate);
                    glRotatef(90F, currentDir.offsetZ, currentDir.offsetY, currentDir.offsetX);
                }

                glScalef(1.95F, 1.55F, 1.95F);

                modelJarConnection.renderAll();

                glPopMatrix();
            }

            glPopMatrix();
        } else if (type.isAlembic()) {
            glPushMatrix();

            glTranslatef(xDisplace, yDisplace, zDisplace);

            modelThaumicPipe.renderArm(index);

            glPopMatrix();
        }
    }

    private void renderNode(boolean flag) {
        glPushMatrix();
        RenderUtils.bindTexture(TextureMaps.THAUMIC_PIPE_CENTRE[TextureMaps.THAUMIC_TEXTURE_INDEX]);

        float scale = 1.01F;
        if (flag)
            scale += 0.32F;

        glScalef(scale, scale, scale);

        modelThaumicPipe.renderPart("Centre");
        glPopMatrix();
    }

    @Override
    public void renderAt(PipePartAbstract part, double x, double y, double z, float frame) {
        if (part instanceof ThaumicPipePart)
            render((ThaumicPipePart) part, x, y, z, frame);
    }
}
