import express from 'express';
import cors from 'cors';
import { router } from './routes/index.js';
import { errorHandler, notFoundHandler } from './middleware/error.js';

export function createApp() {
  const app = express();

  app.use(cors());
  app.use(express.json());

  // Simple healthcheck
  app.get('/health', (_req, res) => {
    res.json({ status: 'ok', service: 'stockhome-api', time: new Date().toISOString() });
  });

  app.use('/api', router);

  app.use(notFoundHandler);
  app.use(errorHandler);

  return app;
}
