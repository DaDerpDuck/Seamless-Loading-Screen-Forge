function initializeCoreMod() {
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

	var AbstractInsnNode = Java.type('org.objectweb.asm.tree.AbstractInsnNode');
	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

    return {
        'Minecraft#createLevel': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': ASMAPI.mapMethod('createLevel'),
                'methodDesc': '(Ljava/lang/String;Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/core/RegistryAccess$RegistryHolder;Lnet/minecraft/world/level/levelgen/WorldGenSettings;)V'
            },
            'transformer': function(methodNode) {
				var list = ASMAPI.listOf(
					new VarInsnNode(Opcodes.ALOAD, 1),
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "onCreateLevel", "(Ljava/lang/String;)V", ASMAPI.MethodType.STATIC)
				);
				methodNode.instructions.insert(list);

            	return methodNode;
            }
        },
        'Minecraft#clearLevel': {
        	'target': {
        		'type': 'METHOD',
        		'class': 'net.minecraft.client.Minecraft',
        		'methodName': ASMAPI.mapMethod('clearLevel'),
        		'methodDesc': '(Lnet/minecraft/client/gui/screens/Screen;)V'
        	},
        	'transformer': function(methodNode) {
        		var skip = new LabelNode();

        		var list = ASMAPI.listOf(
        			new VarInsnNode(Opcodes.ALOAD, 1),
        			ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "clearLevelScreenshot", "(Lnet/minecraft/client/gui/screens/Screen;)Z", ASMAPI.MethodType.STATIC),
        			new JumpInsnNode(Opcodes.IFEQ, skip),
        			new VarInsnNode(Opcodes.ALOAD, 0),
        			new InsnNode(Opcodes.ICONST_1),
        			ASMAPI.buildMethodCall("net/minecraft/client/Minecraft", ASMAPI.mapMethod("runTick"), "(Z)V", ASMAPI.MethodType.VIRTUAL),
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
				'methodName': ASMAPI.mapMethod('loadWorld'),
				'methodDesc': '()V'
			},
			'transformer': function(methodNode) {
				var list = ASMAPI.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/worldselection/WorldSelectionList$WorldListEntry", ASMAPI.mapField("summary"), "Lnet/minecraft/world/level/storage/LevelSummary;"),
					ASMAPI.buildMethodCall("net/minecraft/world/level/storage/LevelSummary", ASMAPI.mapMethod("getLevelName"), "()Ljava/lang/String;", ASMAPI.MethodType.VIRTUAL),
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "preLoadLevel", "(Ljava/lang/String;)V", ASMAPI.MethodType.STATIC)
				);
				methodNode.instructions.insert(list);

				return methodNode;
			}
        },
        'MouseHandler#setIgnoreFirstMove': {
        	'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.MouseHandler',
				'methodName': ASMAPI.mapMethod('setIgnoreFirstMove'),
				'methodDesc': '()V'
			},
			'transformer': function(methodNode) {
				var skip = new LabelNode();

				var list = ASMAPI.listOf(
					ASMAPI.buildMethodCall("com/daderpduck/seamless_loading_screen/events/Transformer", "turnPlayer", "()Z", ASMAPI.MethodType.STATIC),
					new JumpInsnNode(Opcodes.IFEQ, skip),
					new InsnNode(Opcodes.DCONST_0),
					new FieldInsnNode(Opcodes.SETFIELD, "net/minecraft/client/MouseHandler", ASMAPI.mapField("accumulatedDX"), "D"),
					new InsnNode(Opcodes.DCONST_0),
					new FieldInsnNode(Opcodes.SETFIELD, "net/minecraft/client/MouseHandler", ASMAPI.mapField("accumulatedDY"), "D"),
					new InsnNode(Opcodes.RETURN)
					skip
				);
				methodNode.instructions.insert(list);

				return methodNode;
			}
        }
    };
}