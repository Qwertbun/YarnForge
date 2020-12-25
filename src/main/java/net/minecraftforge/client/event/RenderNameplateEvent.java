/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

/**
 * RenderNameplateEvent is fired whenever the entity renderer attempts to render a name plate/tag of an entity.
 * <br>
 * {@link #nameplateContent} contains the content being rendered on the name plate/tag. This can be changed by mods.<br>
 * {@link #originalContent} contains the original content being rendered on the name plate/tag. This cannot be
 * changed by mods.<br>
 * {@link #entityRenderer} contains the entity renderer instance that renders the name plate/tag. This cannot be
 * changed by mods.<br>
 * {@link #matrixStack} contains the matrix stack instance involved in rendering the name plate/tag. This cannot
 * be changed by mods.<br>
 * {@link #renderTypeBuffer} contains the render type buffer instance involved in rendering the name plate/tag.
 * This cannot be changed by mods.<br>
 * {@link #packedLight} contains the sky and block light values used in rendering the name plate/tag.<br>
 * {@link #partialTicks} contains the partial ticks used in rendering the name plate/tag. This cannot be changed by mods.<br>
 * <br>
 * This event has a result. {@link HasResult}. <br>
 * ALLOW will force-render name plate/tag, DEFAULT will ignore the hook and continue using the vanilla check
 * & DENY will prevent name plate/tag from rendering<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Event.HasResult
public class RenderNameplateEvent extends EntityEvent
{

    private Text nameplateContent;
    private final Text originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final MatrixStack matrixStack;
    private final VertexConsumerProvider renderTypeBuffer;
    private final int packedLight;
    private final float partialTicks;
    
    public RenderNameplateEvent(Entity entity, Text content, EntityRenderer<?> entityRenderer, MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer, int packedLight, float partialTicks)
    {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.matrixStack = matrixStack;
        this.renderTypeBuffer = renderTypeBuffer;
        this.packedLight = packedLight;
        this.partialTicks = partialTicks;
    }

    /**
     * Sets the content that is to be rendered on the name plate/tag
     */
    public void setContent(Text contents)
    {
        this.nameplateContent = contents;
    }

    /**
     * The content being rendered on the name plate/tag
     */
    public Text getContent()
    {
        return this.nameplateContent;
    }

    /**
     * The original content being rendered on the name plate/tag
     */
    public Text getOriginalContent()
    {
        return this.originalContent;
    }

    /**
     * The entity renderer that renders the name plate/tag, if it was provided
     */
    public EntityRenderer<?> getEntityRenderer()
    {
        return this.entityRenderer;
    }

    /**
     * The matrix stack used during the rendering of the name plate/tag
     */
    public MatrixStack getMatrixStack()
    {
        return this.matrixStack;
    }

    /**
     * The render type buffer used during the rendering of the name plate/tag
     */
    public VertexConsumerProvider getRenderTypeBuffer()
    {
        return this.renderTypeBuffer;
    }

    /**
     * The packed values of sky and block light used during the rendering of the name plate/tag
     */
    public int getPackedLight()
    {
        return this.packedLight;
    }
    
    /**
     * The partial ticks used during the rendering of the name plate/tag
     */
    public float getPartialTicks()
    {
        return this.partialTicks;
    }
}
