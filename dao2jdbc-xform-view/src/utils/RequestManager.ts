const controllers = new Map<string, AbortController>();

export const RequestManager = {
  create: (key: string) => {
    const controller = new AbortController();
    controllers.set(key, controller);
    return controller.signal;
  },

  cancel: (key: string) => {
    const controller = controllers.get(key);
    if (controller) {
      controller.abort();
      controllers.delete(key);
    }
  },

  cancelAll: () => {
    for (const [, controller] of controllers) {
      controller.abort();
    }
    controllers.clear();
  }
};