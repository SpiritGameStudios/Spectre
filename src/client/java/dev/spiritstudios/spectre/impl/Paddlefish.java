package dev.spiritstudios.spectre.impl;// Made with Blockbench 5.0.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class Paddlefish extends EntityModel<EntityRenderState> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Spectre.id("paddlefish"), "bb");
	private final ModelPart body;
	private final ModelPart top_left_fin;
	private final ModelPart bottom_left_fin;
	private final ModelPart top_right_fin;
	private final ModelPart bottom_right_fin;

	public Paddlefish(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.top_left_fin = this.body.getChild("top_left_fin");
		this.bottom_left_fin = this.body.getChild("bottom_left_fin");
		this.top_right_fin = this.body.getChild("top_right_fin");
		this.bottom_right_fin = this.body.getChild("bottom_right_fin");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, 0.0F));

		PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7071F, -0.7071F, 0.7854F, 0.0F, 0.0F));

		PartDefinition top_left_fin = body.addOrReplaceChild("top_left_fin", CubeListBuilder.create().texOffs(0, -9).addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(9, -9).addBox(0.0F, -0.5F, 9.0F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, -0.5F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bottom_left_fin = body.addOrReplaceChild("bottom_left_fin", CubeListBuilder.create().texOffs(0, -4).addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(9, -4).addBox(0.0F, -4.5F, 9.0F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, -0.5F, -0.7854F, 0.0F, 0.0F));

		PartDefinition top_right_fin = body.addOrReplaceChild("top_right_fin", CubeListBuilder.create().texOffs(0, -9).mirror().addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(9, -9).mirror().addBox(0.0F, -0.5F, 9.0F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -2.0F, -0.5F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bottom_right_fin = body.addOrReplaceChild("bottom_right_fin", CubeListBuilder.create().texOffs(0, -4).mirror().addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(9, -4).mirror().addBox(0.0F, -4.5F, 9.0F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 1.0F, -0.5F, -0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
}
