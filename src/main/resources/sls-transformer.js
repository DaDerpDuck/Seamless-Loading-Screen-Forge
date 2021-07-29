function initializeCoreMod() {
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

	var AbstractInsnNode = Java.type('org.objectweb.asm.tree.AbstractInsnNode');
	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
    var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');

    return {
        'Minecraft#createLevel': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': ASMAPI.mapMethod('m_91202_'),
                'methodDesc': '(Ljava/lang/String;Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/core/RegistryAccess$RegistryHolder;Lnet/minecraft/world/level/levelgen/WorldGenSettings;)V'
            },
            'transformer': function(methodNode) {
				var list = ASMAPI.listOf(
					new VarInsnNode(Opcodes.ALOAD, 1),
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "postPreLoadLevel", "(Ljava/lang/String;)V", ASMAPI.MethodType.STATIC)
				);
				methodNode.instructions.insert(list);

            	return methodNode;
            }
        },
        'Minecraft#clearLevel': {
        	'target': {
        		'type': 'METHOD',
        		'class': 'net.minecraft.client.Minecraft',
        		'methodName': ASMAPI.mapMethod('m_91320_'),
        		'methodDesc': '(Lnet/minecraft/client/gui/screens/Screen;)V'
        	},
        	'transformer': function(methodNode) {
        		var skip = new LabelNode();

        		var list = ASMAPI.listOf(
        			new VarInsnNode(Opcodes.ALOAD, 1),
        			ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "postClearLevel", "(Lnet/minecraft/client/gui/screens/Screen;)Z", ASMAPI.MethodType.STATIC),
        			new JumpInsnNode(Opcodes.IFEQ, skip),
        			new VarInsnNode(Opcodes.ALOAD, 0),
        			new InsnNode(Opcodes.ICONST_1),
        			ASMAPI.buildMethodCall("net/minecraft/client/Minecraft", ASMAPI.mapMethod("m_91383_"), "(Z)V", ASMAPI.MethodType.VIRTUAL),
					new InsnNode(Opcodes.RETURN),
					skip
        		);
        		methodNode.instructions.insert(list);

        		return methodNode;
        	}
        },
        'WorldSelectionList#loadWorld': {
        	'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.gui.screens.worldselection.WorldSelectionList$WorldListEntry',
				'methodName': ASMAPI.mapMethod('m_101744_'),
				'methodDesc': '()V'
			},
			'transformer': function(methodNode) {
				var list = ASMAPI.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/worldselection/WorldSelectionList$WorldListEntry", ASMAPI.mapField("f_101695_"), "Lnet/minecraft/world/level/storage/LevelSummary;"),
					ASMAPI.buildMethodCall("net/minecraft/world/level/storage/LevelSummary", ASMAPI.mapMethod("m_78358_"), "()Ljava/lang/String;", ASMAPI.MethodType.VIRTUAL),
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "postPreLoadLevel", "(Ljava/lang/String;)V", ASMAPI.MethodType.STATIC)
				);
				methodNode.instructions.insert(list);

				return methodNode;
			}
        },
        'MouseHandler#turnPlayer': {
        	'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.MouseHandler',
				'methodName': ASMAPI.mapMethod('m_91523_'),
				'methodDesc': '()V'
			},
			'transformer': function(methodNode) {
				var skip = new LabelNode();

				var list = ASMAPI.listOf(
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "checkLockTurn", "()Z", ASMAPI.MethodType.STATIC),
					new JumpInsnNode(Opcodes.IFEQ, skip),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new InsnNode(Opcodes.DCONST_0),
					new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/MouseHandler", ASMAPI.mapField("f_91516_"), "D"),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new InsnNode(Opcodes.DCONST_0),
					new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/MouseHandler", ASMAPI.mapField("f_91517_"), "D"),
					new InsnNode(Opcodes.RETURN),
					skip
				);
				methodNode.instructions.insert(list);

				return methodNode;
			}
        },
        'LevelLoadingScreen#renderChunks': {
        	'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.gui.screens.LevelLoadingScreen',
				'methodName': ASMAPI.mapMethod('m_96149_'),
				'methodDesc': '(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/server/level/progress/StoringChunkProgressListener;IIII)V'
			},
			'transformer': function(methodNode) {
				var iterator = methodNode.instructions.iterator();
				while (iterator.hasNext()) {
					var insnNode = iterator.next();
					if (insnNode.getOpcode() === Opcodes.INVOKESTATIC && insnNode.owner === "net/minecraft/client/gui/screens/LevelLoadingScreen" && insnNode.name === ASMAPI.mapMethod("m_93172_") && insnNode.desc === "(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V") {
						insnNode.owner = "com/daderpduck/seamless_loading_screen/events/Transformer";
						insnNode.name = "changeChunkLoadFill";
					}
				}

				return methodNode;
			}
        },
        'SaveFormat$LevelSave#deleteLevel': {
        	'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess',
				'methodName': ASMAPI.mapMethod('m_78311_'),
				'methodDesc': '()V'
			},
			'transformer': function(methodNode) {
				var iterator = methodNode.instructions.iterator();
				var tail;
				while (iterator.hasNext()) {
					var insnNode = iterator.next();
					if (insnNode.getOpcode() === Opcodes.RETURN) {
						tail = insnNode;
					}
				}

				if (tail) {
					var list = ASMAPI.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess", ASMAPI.mapField("f_78271_"), "Ljava/nio/file/Path;"),
						ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "postDeleteSave", "(Ljava/nio/file/Path;)V", ASMAPI.MethodType.STATIC)
					);
					methodNode.instructions.insertBefore(tail, list);
				}

				return methodNode;
			}
        },
        'RealmsMainScreen#play': {
			'target': {
				'type': 'METHOD',
				'class': 'com.mojang.realmsclient.RealmsMainScreen',
				'methodName': ASMAPI.mapMethod('m_86515_'),
				'methodDesc': '(Lcom/mojang/realmsclient/dto/RealmsServer;Lnet/minecraft/client/gui/screens/Screen;)V'
			},
			'transformer': function(methodNode) {
				var list = ASMAPI.listOf(
					new VarInsnNode(Opcodes.ALOAD, 1),
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "postRealmJoin", "(Lcom/mojang/realmsclient/dto/RealmsServer;)V", ASMAPI.MethodType.STATIC)
				);
				methodNode.instructions.insert(list);
				return methodNode;
			}
		}
    };
}