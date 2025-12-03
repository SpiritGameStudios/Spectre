package dev.spiritstudios.spectre.impl.client;// Made with Blockbench 5.0.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import dev.spiritstudios.spectre.impl.Spectre;
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

public class BloomrayModel<T extends EntityRenderState> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Spectre.id("bloomray"), "main");
	private final ModelPart main;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart left2;
	private final ModelPart right2;
	private final ModelPart right;
	private final ModelPart left;
	private final ModelPart head;
	private final ModelPart right3;
	private final ModelPart left3;

	public BloomrayModel(ModelPart root) {
		super(root);
		this.main = root.getChild("main");
		this.body = this.main.getChild("body");
		this.tail = this.body.getChild("tail");
		this.left2 = this.body.getChild("left2");
		this.right2 = this.body.getChild("right2");
		this.right = this.body.getChild("right");
		this.left = this.body.getChild("left");
		this.head = this.body.getChild("head");
		this.right3 = this.head.getChild("right3");
		this.left3 = this.head.getChild("left3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = main.addOrReplaceChild("body", CubeListBuilder.create().texOffs(96, 88).addBox(-6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(96, 0).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 40.0F, new CubeDeformation(0.0F))
		.texOffs(0, 56).addBox(0.0F, -10.0F, 0.0F, 0.0F, 8.0F, 48.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 0.0F, 8.0F, 48.0F, new CubeDeformation(0.0F))
		.texOffs(92, 124).addBox(-10.0F, -2.0F, 10.0F, 8.0F, 0.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(64, 124).addBox(2.0F, -2.0F, 10.0F, 8.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 4.0F));

		PartDefinition left2 = body.addOrReplaceChild("left2", CubeListBuilder.create().texOffs(32, 124).addBox(0.0F, 0.0F, -8.0F, 0.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 4.0F, -8.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition right2 = body.addOrReplaceChild("right2", CubeListBuilder.create().texOffs(32, 124).addBox(0.0F, 0.0F, -8.0F, 0.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 4.0F, -8.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition right = body.addOrReplaceChild("right", CubeListBuilder.create().texOffs(96, 66).addBox(-30.0F, -1.0F, -5.5F, 30.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(72, 112).addBox(-22.0F, -1.0F, 14.5F, 26.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 1.0F, -10.5F));

		PartDefinition left = body.addOrReplaceChild("left", CubeListBuilder.create().texOffs(96, 44).addBox(0.0F, -1.0F, -5.5F, 30.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
		.texOffs(0, 112).addBox(-4.0F, -1.0F, 14.5F, 26.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 1.0F, -10.5F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(64, 134).addBox(-4.0F, -2.0F, -7.0F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, -15.0F));

		PartDefinition right3 = head.addOrReplaceChild("right3", CubeListBuilder.create().texOffs(94, 134).addBox(0.0F, 0.0F, -4.0F, 0.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, -5.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition left3 = head.addOrReplaceChild("left3", CubeListBuilder.create().texOffs(106, 134).addBox(0.0F, 0.0F, -4.0F, 0.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 2.0F, -5.0F, 0.0F, 0.0F, 0.4363F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}
}
